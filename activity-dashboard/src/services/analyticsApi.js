
const API_BASE_URL = '/api/analytics';

export async function getAnalyticsSummary() {
  const response = await fetch(`${API_BASE_URL}/summary`);
  if (!response.ok) throw new Error('Failed to fetch summary');
  return response.json();
}

export async function getUserAnalytics() {
  const response = await fetch(`${API_BASE_URL}/users`);
  if (!response.ok) throw new Error('Failed to fetch users');
  return response.json();
}

export async function getModuleAnalytics() {
  const response = await fetch(`${API_BASE_URL}/modules`);
  if (!response.ok) throw new Error('Failed to fetch modules');
  return response.json();
}

export async function getRecentEvents() {
  const response = await fetch(`${API_BASE_URL}/events`);
  if (!response.ok) throw new Error('Failed to fetch events');
  return response.json();
}

export async function getDownloadAnalytics() {
  const response = await fetch(`${API_BASE_URL}/downloads`);
  if (!response.ok) throw new Error('Failed to fetch downloads');
  return response.json();
}
