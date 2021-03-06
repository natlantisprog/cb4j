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

package io.github.benas.cb4j.core.validator.test;

import io.github.benas.cb4j.core.model.Field;
import io.github.benas.cb4j.core.validator.EmailFieldValidator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit Tests class for {@link EmailFieldValidator}
 * @author benas (md.benhassine@gmail.com)
 */
public class EmailFieldValidatorTest {

    private EmailFieldValidator emailFieldValidator;
    private Field field;

    @Before
    public void setUp() throws Exception {
        emailFieldValidator = new EmailFieldValidator();
        field = new Field(1,null);
    }

    @After
    public void tearDown() throws Exception {
        emailFieldValidator = null;
        field = null;
        System.gc();
    }

    @Test
    public void testValidEmail() throws Exception {
        field = new Field(1,"email@server.com");
        assertTrue(emailFieldValidator.isValid(field));
    }

    @Test
    public void testInvalidEmail() throws Exception {
        field = new Field(1,"email.server.com");
        assertFalse(emailFieldValidator.isValid(field));
    }
}
