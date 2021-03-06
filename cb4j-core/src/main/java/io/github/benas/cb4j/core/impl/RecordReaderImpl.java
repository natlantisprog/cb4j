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

import io.github.benas.cb4j.core.api.RecordReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Implementation of {@link RecordReader}.<br/>
 * This class is not intended to be used nor extended by framework users
 *
 * @author benas (md.benhassine@gmail.com)
 */
public final class RecordReaderImpl implements RecordReader {

    /**
     * Scanner to read input file
     */
    private Scanner scanner;

    /**
     * A second scanner used to calculate the number of records in the input file.
     * The main scanner may be used instead but since the {@link Scanner} class does not have a method to rewind it to the
     * beginning of the file ( {@link java.util.Scanner#reset()} does not rewind the scanner), another scanner instance is needed.
     */
    private Scanner recordCounterScanner;

    /**
     * Header record.
     */
    private String headerRecord;

    public RecordReaderImpl(final String input, final String charset, final boolean skipHeader) throws FileNotFoundException {

        File file = new File(input);

        scanner = new Scanner(file, charset);
        if (skipHeader && hasNextRecord()) {
            scanner.nextLine();
        }

        recordCounterScanner = new Scanner(file, charset);
        if (skipHeader && hasNextRecord()) {
            recordCounterScanner.nextLine();
        }

        /*
         * a scanner to get header record (do not interfere with skipHeader param)
         */
        Scanner s = new Scanner(file, charset);
        headerRecord = s.nextLine();
        s.close();

    }

    /**
     * {@inheritDoc}
     */
    public String readNextRecord() {
        return scanner.nextLine();
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNextRecord() {
        return scanner.hasNextLine();
    }

    /**
     * {@inheritDoc}
     */
    public long getTotalRecordsNumber() {
        long totalRecordsNumber = 0;
        while (recordCounterScanner.hasNextLine()) {
            totalRecordsNumber++;
            recordCounterScanner.nextLine();
        }
        recordCounterScanner.close();
        return totalRecordsNumber;
    }

    /**
     * {@inheritDoc}
     */
    public String getHeaderRecord() {
        return headerRecord;
    }

    /**
     * {@inheritDoc}
     */
    public void close() {
        scanner.close();
    }
}
