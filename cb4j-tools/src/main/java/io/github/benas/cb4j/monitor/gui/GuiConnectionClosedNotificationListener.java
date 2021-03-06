/*
 * The MIT License
 *
 *    Copyright (c) 2013, benas (md.benhassine@gmail.com)
 *
 *    Permission is hereby granted, free of charge, to any person obtaining a copy
 *    of this software and associated documentation files (the "Software"), to deal
 *    in the Software without restriction, including without limitation the rights
 *    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *    copies of the Software, and to permit persons to whom the Software is
 *    furnished to do so, subject to the following conditions:
 *
 *    The above copyright notice and this permission notice shall be included in
 *    all copies or substantial portions of the Software.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *    THE SOFTWARE.
 */

package io.github.benas.cb4j.monitor.gui;

import io.github.benas.cb4j.core.api.BatchReport;

import javax.management.Notification;
import javax.management.NotificationListener;

/**
 * A listener for JMX connection-closed event {@link javax.management.remote.JMXConnectionNotification#CLOSED}.
 * @author benas (md.benhassine@gmail.com)
 */
public class GuiConnectionClosedNotificationListener implements NotificationListener {

    /**
     * The report update notification listener used to get the last update when the connection to JMX server is closed.
     */
    private GuiReportUpdateNotificationListener guiReportUpdateNotificationListener;

    /**
     * The GUI to update when a connection-closed event is received via JMX.
     */
    private CB4JMonitor cb4JMonitor;

    public GuiConnectionClosedNotificationListener(GuiReportUpdateNotificationListener guiReportUpdateNotificationListener, CB4JMonitor cb4JMonitor) {
        this.guiReportUpdateNotificationListener = guiReportUpdateNotificationListener;
        this.cb4JMonitor = cb4JMonitor;
    }

    @Override
    public void handleNotification(Notification notification, Object handback) {
        BatchReport batchReport = guiReportUpdateNotificationListener.getBatchReport();
        cb4JMonitor.lastUpdate(batchReport);
    }
}