
import { useState } from 'react';
import * as XLSX from 'xlsx';
import { jsPDF } from 'jspdf';

import { trackActivity } from '../services/activityApi';

import {
  getAnalyticsSummary,
  getUserAnalytics,
  getModuleAnalytics,
  getRecentEvents,
  getDownloadAnalytics,
} from '../services/analyticsApi';

function ActivityActions() {
  const [status, setStatus] = useState('');
  const [isDownloading, setIsDownloading] = useState(false);

  const fetchReportData = async () => {
    const [
      summary,
      users,
      modules,
      events,
      downloads,
    ] = await Promise.all([
      getAnalyticsSummary(),
      getUserAnalytics(),
      getModuleAnalytics(),
      getRecentEvents(),
      getDownloadAnalytics(),
    ]);

    return { summary, users, modules, events, downloads };
  };

  const handleButtonClick = async () => {
    try {
      setStatus('Sending event...');

      await trackActivity({
        eventType: 'BUTTON_CLICKED',
        buttonId: 'demo_button_click',
        userId: 'USER104',
        module: 'DASHBOARD',
        fileName: null,
      });

      setStatus('Button click tracked successfully!');
    } catch (error) {
      setStatus(error.message || 'Failed to track event');
    }
  };

  // Generate Excel using live analytics data
  const handleExcelDownload = async () => {
    try {
      setIsDownloading(true);
      setStatus('Fetching live analytics data...');

      const data = await fetchReportData();

      const workbook = XLSX.utils.book_new();

      const summaryRows = [
        { Metric: 'Total Events', Value: data.summary.totalEvents },
        ...data.summary.eventsByType.map((item) => ({
          Metric: item.eventType,
          Value: item.total,
        })),
        {
          Metric: 'Excel Downloads',
          Value: data.downloads.excelDownloads,
        },
        {
          Metric: 'PDF Downloads',
          Value: data.downloads.pdfDownloads,
        },
      ];

      const sheets = [
        {
          name: 'Summary',
          rows: summaryRows,
        },
        {
          name: 'User Analytics',
          rows: data.users,
        },
        {
          name: 'Module Analytics',
          rows: data.modules,
        },
        {
          name: 'Recent Activities',
          rows: data.events.map((event) => ({
            EventType: event.eventType,
            UserID: event.userId,
            Module: event.module,
            ButtonID: event.buttonId,
            FileName: event.fileName,
            Timestamp: event.createdAt,
          })),
        },
      ];

      sheets.forEach(({ name, rows }) => {
        const worksheet = XLSX.utils.json_to_sheet(rows);
        XLSX.utils.book_append_sheet(workbook, worksheet, name);
      });

      const fileName = 'activity_report.xlsx';

      XLSX.writeFile(workbook, fileName);

      await trackActivity({
        eventType: 'EXCEL_DOWNLOADED',
        buttonId: 'download_excel',
        userId: 'USER104',
        module: 'ASSET_MANAGEMENT',
        fileName,
      });

      setStatus('Live Excel report downloaded and tracked successfully!');
    } catch (error) {
      setStatus(error.message || 'Excel report generation failed');
    } finally {
      setIsDownloading(false);
    }
  };

  // Generate PDF using live analytics data
  const handlePdfDownload = async () => {
    try {
      setIsDownloading(true);
      setStatus('Fetching live analytics data...');

      const data = await fetchReportData();

      const doc = new jsPDF();

      let y = 20;

      const addLine = (text, fontSize = 10) => {
        doc.setFontSize(fontSize);

        const lines = doc.splitTextToSize(String(text ?? '-'), 180);

        lines.forEach((line) => {
          if (y > 280) {
            doc.addPage();
            y = 20;
          }

          doc.text(line, 15, y);
          y += 7;
        });
      };

      addLine('ActivityTrack Analytics Report', 18);
      y += 5;

      addLine(`Total Events: ${data.summary.totalEvents}`, 12);
      y += 3;

      addLine('Event Summary', 14);

      data.summary.eventsByType.forEach((item) => {
        addLine(`${item.eventType}: ${item.total}`);
      });

      addLine(`Excel Downloads: ${data.downloads.excelDownloads}`);
      addLine(`PDF Downloads: ${data.downloads.pdfDownloads}`);

      y += 5;
      addLine('User Analytics', 14);

      data.users.forEach((user) => {
        addLine(`${user.userId}: ${user.total} events`);
      });

      y += 5;
      addLine('Module Analytics', 14);

      data.modules.forEach((module) => {
        addLine(`${module.module}: ${module.total} events`);
      });

      y += 5;
      addLine('Recent Activities', 14);

      data.events.forEach((event) => {
        addLine(
          `${event.eventType} | ${event.userId} | ${event.module} | ${event.fileName || event.buttonId || '-'}`
        );

        addLine(`Timestamp: ${event.createdAt || '-'}`);
        y += 2;
      });

      const fileName = 'activity_report.pdf';

      doc.save(fileName);

      await trackActivity({
        eventType: 'PDF_DOWNLOADED',
        buttonId: 'download_pdf',
        userId: 'USER104',
        module: 'REPORTING',
        fileName,
      });

      setStatus('Live PDF report downloaded and tracked successfully!');
    } catch (error) {
      setStatus(error.message || 'PDF report generation failed');
    } finally {
      setIsDownloading(false);
    }
  };

  return (
    <section className="panel">
      <div className="panel-heading">
        <div>
          <h2>Activity Tracking Demo</h2>
          <p>
            Track button clicks and download live analytics reports
          </p>
        </div>
      </div>

      <div
        style={{
          display: 'flex',
          flexWrap: 'wrap',
          gap: '12px',
        }}
      >
        <button
          className="refresh-btn"
          onClick={handleButtonClick}
          disabled={isDownloading}
        >
          Track Button Click
        </button>

        <button
          className="refresh-btn"
          onClick={handleExcelDownload}
          disabled={isDownloading}
        >
          Download Excel Report
        </button>

        <button
          className="refresh-btn"
          onClick={handlePdfDownload}
          disabled={isDownloading}
        >
          Download PDF Report
        </button>
      </div>

      {status && (
        <p style={{ marginTop: '15px' }}>
          {status}
        </p>
      )}
    </section>
  );
}

export default ActivityActions;
