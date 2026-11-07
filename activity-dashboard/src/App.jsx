
import { useEffect, useState } from 'react';

import ActivityActions from './components/ActivityActions';

import {
  Activity,
  Users,
  MousePointerClick,
  Download,
  RefreshCw,
  FileSpreadsheet,
  FileText,
  LayoutDashboard,
} from 'lucide-react';

import {
  getAnalyticsSummary,
  getUserAnalytics,
  getModuleAnalytics,
  getRecentEvents,
  getDownloadAnalytics,
} from './services/analyticsApi';

import {
  ResponsiveContainer,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  PieChart,
  Pie,
  Cell,
  Legend,
} from 'recharts';

import './App.css';

const COLORS = ['#6366f1', '#06b6d4', '#f59e0b', '#10b981'];

function App() {
  // Dashboard states
  const [summary, setSummary] = useState(null);
  const [users, setUsers] = useState([]);
  const [modules, setModules] = useState([]);
  const [events, setEvents] = useState([]);
  const [downloads, setDownloads] = useState(null);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Fetch analytics from Spring Boot APIs
  const loadAnalytics = async () => {
    try {
      const [
        summaryData,
        userData,
        moduleData,
        eventData,
        downloadData,
      ] = await Promise.all([
        getAnalyticsSummary(),
        getUserAnalytics(),
        getModuleAnalytics(),
        getRecentEvents(),
        getDownloadAnalytics(),
      ]);

      setError('');
      setSummary(summaryData);
      setUsers(userData);
      setModules(moduleData);
      setEvents(eventData);
      setDownloads(downloadData);
    } catch (err) {
      setError(err.message || 'Unable to load analytics');
    } finally {
      setLoading(false);
    }
  };

  // Initial load and automatic refresh every 15 seconds
  useEffect(() => {
    const initialLoad = setTimeout(() => {
      loadAnalytics();
    }, 0);

    const interval = setInterval(() => {
      loadAnalytics();
    }, 15000);

    return () => {
      clearTimeout(initialLoad);
      clearInterval(interval);
    };
  }, []);

  // Calculate button clicks
  const buttonClicks =
    summary?.eventsByType?.find(
      (item) => item.eventType === 'BUTTON_CLICKED'
    )?.total || 0;

  // Format event timestamps
  const formatDate = (date) => {
    if (!date) return '-';

    return new Date(date).toLocaleString();
  };

  // Loading screen
  if (loading) {
    return (
      <div className="loading">
        Loading activity analytics...
      </div>
    );
  }

  return (
    <div className="dashboard">

      {/* Sidebar */}
      <aside className="sidebar">

        <div className="brand">
          <Activity size={27} />
          <span>ActivityTrack</span>
        </div>

        <div className="nav-label">
          MAIN MENU
        </div>

        <div className="nav-item active">
          <LayoutDashboard size={19} />
          Dashboard
        </div>

        <div className="nav-item">
          <Users size={19} />
          User Analytics
        </div>

        <div className="nav-item">
          <Download size={19} />
          Downloads
        </div>

        <div className="sidebar-footer">
          <span className="status-dot"></span>
          Kafka Connected
        </div>

      </aside>

      {/* Main Dashboard */}
      <main className="main-content">

        {/* Header */}
        <header className="topbar">

          <div>
            <div className="breadcrumb">
              Analytics / Dashboard
            </div>

            <h1>Activity Dashboard</h1>

            <p>
              Monitor application usage and user activities
            </p>
          </div>

          <button
            className="refresh-btn"
            onClick={loadAnalytics}
          >
            <RefreshCw size={16} />
            Refresh
          </button>

        </header>

        {/* Error Message */}
        {error && (
          <div className="error-message">
            {error}
          </div>
        )}

        {/* Summary Cards */}
        <section className="stats-grid">

          <StatCard
            title="Total Events"
            value={summary?.totalEvents ?? 0}
            icon={<Activity />}
            color="purple"
          />

          <StatCard
            title="Button Clicks"
            value={buttonClicks}
            icon={<MousePointerClick />}
            color="blue"
          />

          <StatCard
            title="Excel Downloads"
            value={downloads?.excelDownloads ?? 0}
            icon={<FileSpreadsheet />}
            color="green"
          />

          <StatCard
            title="PDF Downloads"
            value={downloads?.pdfDownloads ?? 0}
            icon={<FileText />}
            color="orange"
          />

        </section>

        {/* Activity Tracking Demo */}
        <ActivityActions />

        {/* Analytics Charts */}
        <section className="charts-grid">

          {/* User Activity Chart */}
          <div className="panel">

            <div className="panel-heading">
              <div>
                <h2>User Activity</h2>
                <p>Events recorded per user</p>
              </div>
            </div>

            <ResponsiveContainer width="100%" height={280}>
              <BarChart data={users}>

                <CartesianGrid
                  strokeDasharray="3 3"
                  vertical={false}
                />

                <XAxis dataKey="userId" />

                <YAxis allowDecimals={false} />

                <Tooltip />

                <Bar
                  dataKey="total"
                  fill="#6366f1"
                  radius={[6, 6, 0, 0]}
                />

              </BarChart>
            </ResponsiveContainer>

          </div>

          {/* Module Usage Chart */}
          <div className="panel">

            <div className="panel-heading">
              <div>
                <h2>Module Usage</h2>
                <p>Activity distribution by module</p>
              </div>
            </div>

            <ResponsiveContainer width="100%" height={280}>
              <PieChart>

                <Pie
                  data={modules}
                  dataKey="total"
                  nameKey="module"
                  cx="50%"
                  cy="45%"
                  outerRadius={85}
                  label
                >
                  {modules.map((entry, index) => (
                    <Cell
                      key={entry.module}
                      fill={COLORS[index % COLORS.length]}
                    />
                  ))}
                </Pie>

                <Tooltip />

                <Legend />

              </PieChart>
            </ResponsiveContainer>

          </div>

        </section>

        {/* Recent Activities Table */}
        <section className="panel activity-panel">

          <div className="panel-heading">

            <div>
              <h2>Recent Activities</h2>
              <p>Latest recorded application events</p>
            </div>

            <span className="record-count">
              {events.length} records
            </span>

          </div>

          <div className="table-wrapper">

            <table>

              <thead>
                <tr>
                  <th>Event Type</th>
                  <th>User ID</th>
                  <th>Module</th>
                  <th>Button / File</th>
                  <th>Timestamp</th>
                </tr>
              </thead>

              <tbody>

                {events.map((event) => (
                  <tr key={event.id}>

                    <td>
                      <span
                        className={`event-badge ${
                          event.eventType?.toLowerCase()
                        }`}
                      >
                        {event.eventType?.replaceAll('_', ' ') || '-'}
                      </span>
                    </td>

                    <td>{event.userId || '-'}</td>

                    <td>{event.module || '-'}</td>

                    <td>
                      {event.fileName || event.buttonId || '-'}
                    </td>

                    <td>{formatDate(event.createdAt)}</td>

                  </tr>
                ))}

                {events.length === 0 && (
                  <tr>
                    <td colSpan="5">
                      No activity records found.
                    </td>
                  </tr>
                )}

              </tbody>

            </table>

          </div>

        </section>

        {/* Footer */}
        <footer className="dashboard-footer">
          ActivityTrack · Powered by Kafka, Spring Boot and PostgreSQL
        </footer>

      </main>

    </div>
  );
}

// Reusable Summary Card
function StatCard({ title, value, icon, color }) {
  return (
    <div className="stat-card">

      <div>
        <p>{title}</p>
        <h2>{value}</h2>
      </div>

      <div className={`stat-icon ${color}`}>
        {icon}
      </div>

    </div>
  );
}

export default App;
