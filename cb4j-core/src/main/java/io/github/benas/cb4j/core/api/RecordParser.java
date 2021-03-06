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

package io.github.benas.cb4j.core.api;

import io.github.benas.cb4j.core.model.Record;

/**
 * Interface for record parser.<br/>
 *
 * This interface is not intended to be implemented nor used by framework users
 *
 * @author benas (md.benhassine@gmail.com)
 */
public interface RecordParser {

    /**
     * Checks if a record is well formed.<br/>
     * @param record the record to analyse
     * @return Error message or null if the record is well formed.
     */
    String analyseRecord(final String record);

    /**
     * Parses a string and return a parsed {@link Record}.
     * @param record the record as string
     * @param recordNumber the record number in the input file
     * @return a parsed {@link Record}
     */
    Record parseRecord(final String record, long recordNumber);

}
