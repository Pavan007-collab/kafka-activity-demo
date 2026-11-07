const API_URL = '/api/activity';

export async function trackActivity(activity) {
  const response = await fetch(API_URL, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(activity),
  });

  if (!response.ok) {
    throw new Error('Failed to track activity');
  }

  return response.text();
}
