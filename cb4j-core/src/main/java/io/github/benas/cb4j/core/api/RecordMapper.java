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
 * Interface for record/object mapper.<br/>
 * Implementation of this interface should use the same type T used in {@link RecordProcessor} to avoid type casting exception or any type safety warnings
 *
 * @param <T> The domain object type.
 *
 * @author benas (md.benhassine@gmail.com)
 */
public interface RecordMapper<T> {

    /**
     * Maps a record to an Object of type T.
     * @param record The record to map
     * @return The mapped object
     * @throws RecordMappingException thrown if any mapping exception occurs. Implementation should wrap any exception to a {@link RecordMappingException} in order to let CB4J reject the record in the rejected log file
     */
    T mapRecord(final Record record) throws RecordMappingException;

}
