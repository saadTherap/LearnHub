import { sleep } from 'k6';
import { apiGet, apiPost } from './lib/httpClient.js';
import { login, authHeaders } from './lib/helpers.js';

export const options = {
    vus: 1,
    duration: '30s',
    thresholds: {
        http_req_failed: ['rate<0.01'],
        http_req_duration: ['p(95)<500'],
    },
};

export default function () {
    const base = __ENV.BASE_URL || 'http://localhost:8080';
    const headers = authHeaders(login());

    // Check authenticated endpoints
    apiGet(base, '/api/course-configurator/public/courses/byInstructor', headers, 200);
    apiGet(base, '/api/course-configurator/courses/draft', headers, 200);

    // Create a new draft course
    const payload = { name: 'Smoke Test Course' };
    apiPost(base, '/api/course-configurator/courses/draft', payload, headers, 201);

    sleep(1);
}