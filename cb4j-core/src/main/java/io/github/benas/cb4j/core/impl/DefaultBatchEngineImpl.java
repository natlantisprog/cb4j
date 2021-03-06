/*
 * The MIT License
 *
 *  Copyright (c) 2013, benas (md.benhassine@gmail.com)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package io.github.benas.cb4j.core.impl;

import io.github.benas.cb4j.core.api.*;
import io.github.benas.cb4j.core.config.BatchConfiguration;
import io.github.benas.cb4j.core.jmx.BatchMonitor;
import io.github.benas.cb4j.core.model.Record;
import io.github.benas.cb4j.core.util.BatchConstants;
import io.github.benas.cb4j.core.util.BatchStatus;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default implementation of {@link BatchEngine}.<br/>
 *
 * This class may be extended to override initializing and shutdown hooks
 *
 * @author benas (md.benhassine@gmail.com)
 */
public class DefaultBatchEngineImpl implements BatchEngine {

    protected final Logger logger = Logger.getLogger(BatchConstants.LOGGER_CB4J);

    /*
     * CB4J services
     */
    private RecordReader recordReader;

    private RecordParser recordParser;

    protected RecordValidator recordValidator;

    protected BatchReporter batchReporter;

    protected RecordMapper recordMapper;

    protected RecordProcessor recordProcessor;

    protected BatchMonitor batchMonitor;

    protected RollBackHandler rollBackHandler;

    /*
     * User defined parameters used by the engine
     */
    private boolean abortOnFirstReject;

    private boolean abortOnFirstError;

    private boolean abortOnFirstMappingException;

    private boolean skipHeader;

    private boolean jmxEnabled;

    public DefaultBatchEngineImpl(BatchConfiguration batchConfiguration) {
        this.recordReader = batchConfiguration.getRecordReader();
        this.recordParser = batchConfiguration.getRecordParser();
        this.recordValidator = batchConfiguration.getRecordValidator();
        this.recordProcessor = batchConfiguration.getRecordProcessor();
        this.batchReporter = batchConfiguration.getBatchReporter();
        this.recordMapper = batchConfiguration.getRecordMapper();
        this.batchMonitor = batchConfiguration.getBatchMonitor();
        this.rollBackHandler = batchConfiguration.getRollBackHandler();
        this.abortOnFirstReject = batchConfiguration.getAbortOnFirstReject();
        this.abortOnFirstError = batchConfiguration.getAbortOnFirstError();
        this.abortOnFirstMappingException = batchConfiguration.getAbortOnFirstMappingException();
        this.skipHeader = batchConfiguration.getSkipHeader();
        this.jmxEnabled = batchConfiguration.getJmxEnabled();
    }

    /**
     * Initialize the engine.
     * May be overridden with custom initialization code
     */
    public void init() {
        logger.info("Initializing batch...");
        batchReporter.setBatchStatus(BatchStatus.INITIALIZING);
        long totalRecordsNumber = recordReader.getTotalRecordsNumber();
        batchReporter.setTotalInputRecordsNumber(totalRecordsNumber);
        logger.info("Total input records to process = " + totalRecordsNumber);
    }

    public final BatchReport call() { //final : must not be overridden by framework users

        logger.info("CB4J engine is running...");
        batchReporter.setBatchStatus(BatchStatus.RUNNING);
        long currentRecordNumber = skipHeader ? 1 : 0;
        batchReporter.setStartTime(System.currentTimeMillis());

        while (recordReader.hasNextRecord()) {

            currentRecordNumber++;
            batchReporter.setInputRecordsNumber(currentRecordNumber);

            //parse record
            String currentRecord = recordReader.readNextRecord();
            String error = recordParser.analyseRecord(currentRecord);
            if (error != null) {
                batchReporter.reportIgnoredRecord(currentRecord, currentRecordNumber, error);
                continue;
            }

            //validate record
            Record currentParsedRecord = null;
            try {
                currentParsedRecord = recordParser.parseRecord(currentRecord, currentRecordNumber);
                error = recordValidator.validateRecord(currentParsedRecord);
                if (error != null) {
                    batchReporter.reportRejectedRecord(currentParsedRecord, error);
                    if (abortOnFirstReject) {
                        logger.info("Aborting execution on first reject.");
                        break;
                    } else {
                        continue;
                    }
                }
            } catch (Exception e) {
                batchReporter.reportRejectedRecord(currentParsedRecord, "an unexpected validation exception occurred, root cause = " , e);
                if (abortOnFirstReject) {
                    logger.info("Aborting execution on first reject.");
                    break;
                } else {
                    continue;
                }
            }

            //map record to expected type
            Object typedRecord;
            try {
                typedRecord = recordMapper.mapRecord(currentParsedRecord);
            } catch (RecordMappingException e) { //thrown by the user deliberately
                batchReporter.reportRejectedRecord(currentParsedRecord, e.getMessage());
                if (abortOnFirstMappingException) {
                    logger.info("Aborting execution on first mapping exception.");
                    break;
                } else {
                    continue;
                }
            } catch (Exception e) { //thrown unexpectedly
                batchReporter.reportRejectedRecord(currentParsedRecord, "an unexpected mapping exception occurred, root cause = " , e);
                if (abortOnFirstMappingException) {
                    logger.info("Aborting execution on first mapping exception.");
                    break;
                } else {
                    continue;
                }
            }

            //pre process record
            try {
                recordProcessor.preProcessRecord(typedRecord);
            } catch (RecordProcessingException e) { //thrown by the user deliberately
                batchReporter.reportErrorRecord(currentParsedRecord, "a record pre-processing exception occurred, root cause = " + e.getMessage());
                if (abortOnFirstError) {
                    logger.info("Aborting execution on first error.");
                    break;
                } else {
                    continue;
                }
            } catch (Exception e) { //thrown unexpectedly
                batchReporter.reportErrorRecord(currentParsedRecord, "an unexpected record pre-processing exception occurred, root cause = ", e);
                if (abortOnFirstError) {
                    logger.info("Aborting execution on first error.");
                    break;
                } else {
                    continue;
                }
            }

            //process record
            try {
                recordProcessor.processRecord(typedRecord);
            } catch (RecordProcessingException e) { //thrown by the user deliberately
                batchReporter.reportErrorRecord(currentParsedRecord, "a record processing exception occurred, root cause = " + e.getMessage());
                if (abortOnFirstError) {
                    logger.info("Aborting execution on first error.");
                    break;
                } else {
                    continue;
                }
            } catch (Exception e) { //thrown unexpectedly
                batchReporter.reportErrorRecord(currentParsedRecord, "an unexpected record processing exception occurred, root cause = ", e);
                if (rollBackHandler != null) {
                    try {
                        rollBackHandler.rollback(typedRecord);
                        logger.warning("Due to unexpected exception, the processing of record " + typedRecord + " was rolled back.");
                    } catch (Exception rollbackException) {
                        logger.log(Level.SEVERE, "an exception occurred during record " + typedRecord + " rolling back.", rollbackException);
                    }
                }
                if (abortOnFirstError) {
                    logger.info("Aborting execution on first error.");
                    break;
                } else {
                    continue;
                }
            }

            //post process record
            try {
                recordProcessor.postProcessRecord(typedRecord);
            } catch (RecordProcessingException e) { //thrown by the user deliberately
                batchReporter.reportErrorRecord(currentParsedRecord, "a record post-processing exception occurred, root cause = " + e.getMessage());
                if (abortOnFirstError) {
                    logger.info("Aborting execution on first error.");
                    break;
                }
            } catch (Exception e) { //thrown unexpectedly
                batchReporter.reportErrorRecord(currentParsedRecord, "an unexpected exception occurred during record post-processing, root cause = ", e);
                if (abortOnFirstError) {
                    logger.info("Aborting execution on first error.");
                    break;
                }
            }

            //send asynchronous jmx notification about progress
            if (jmxEnabled) {
                batchMonitor.notifyBatchReportUpdate(batchReporter.getBatchReport());
            }
        }

        //close record reader
        recordReader.close();

        batchReporter.setEndTime(System.currentTimeMillis());
        batchReporter.setProcessedRecordsNumber(abortOnFirstMappingException || abortOnFirstError || abortOnFirstReject || skipHeader ? currentRecordNumber - 1 : currentRecordNumber);
        batchReporter.setBatchResultHolder(recordProcessor.getBatchResultHolder());

        //send final asynchronous jmx notification about execution end
        if (jmxEnabled) {
            batchMonitor.notifyBatchReportUpdate(batchReporter.getBatchReport());
        }
        return batchReporter.getBatchReport();
    }

    /**
     * Shutdown the engine.
     * May be overridden with custom shutdown code
     */
    public void shutdown() {
        logger.info("finalizing batch...");
        batchReporter.setBatchStatus(BatchStatus.FINALIZING);

        //generate batch report
        batchReporter.generateReport();
    }

}
