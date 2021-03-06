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

package io.github.benas.cb4j.tutorials.customers;

import io.github.benas.cb4j.core.api.BatchEngine;
import io.github.benas.cb4j.core.config.BatchConfiguration;
import io.github.benas.cb4j.core.config.BatchConfigurationBuilder;
import io.github.benas.cb4j.core.config.BatchConfigurationException;
import io.github.benas.cb4j.core.impl.DefaultBatchEngineImpl;
import io.github.benas.cb4j.core.util.BatchRunner;
import io.github.benas.cb4j.core.validator.DateFieldValidator;
import io.github.benas.cb4j.core.validator.EmailFieldValidator;
import io.github.benas.cb4j.core.validator.FixedLengthNumericFieldValidator;

/**
 * Main class to run the customers tutorial
 * @author benas (md.benhassine@gmail.com)
 */
public class Launcher {

    public static void main(String[] args) {

        if (args == null || args.length == 0 ){
            System.err.println("[CB4J] Configuration parameters not specified, usage : ");
            System.err.println("java io.github.benas.cb4j.tutorials.customers.Launcher path/to/data/file");
            System.err.println("Example : java io.github.benas.cb4j.tutorials.customers.Launcher /data/cb4j/customers.csv");
            System.exit(1);
        }

        /*
         * Create a batch configuration instance
         */
        BatchConfiguration batchConfiguration = new BatchConfigurationBuilder()
                .inputDataFile(args[0])
                .build();

        /*
         * Register field validators
        */
        batchConfiguration.registerFieldValidator(2, new EmailFieldValidator());
        batchConfiguration.registerFieldValidator(3, new DateFieldValidator("dd/MM/yyyy"));
        batchConfiguration.registerFieldValidator(4, new FixedLengthNumericFieldValidator(10));
        batchConfiguration.registerFieldValidator(5, new GenderValidator(1));

        /*
         * Register record mapper and processor
        */
        batchConfiguration.registerRecordMapper(new CustomerMapper());
        batchConfiguration.registerRecordProcessor(new CustomerProcessor());

        /**
         * Configure and run the engine
         */
        try {
            batchConfiguration.configure();
            BatchEngine batchEngine = new DefaultBatchEngineImpl(batchConfiguration);
            BatchRunner batchRunner = new BatchRunner(batchEngine);
            batchRunner.run();
        } catch (BatchConfigurationException e) {
            System.err.println(e.getMessage());
        }

    }

}
