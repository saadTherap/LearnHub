import { sleep } from 'k6';
import { apiGet } from './lib/httpClient.js';
import { login, authHeaders, pick } from './lib/helpers.js';

export function setup() {
    const base = __ENV.BASE_URL || 'http://localhost:8080';
    const token = login();
    const headers = authHeaders(token);
    const coursesRes = apiGet(base, '/api/course-configurator/courses/byInstructor', headers);
    return { authToken: token, courseIds: coursesRes.json().map(c => c.id) };
}

export const options = {
    scenarios: {
        stressy: {
            executor: 'ramping-vus',
            stages: [
                { duration: '1m', target: 50 },
                { duration: '2m', target: 100 },
                { duration: '2m', target: 150 },
                { duration: '2m', target: 200 },
                { duration: '1m', target: 0 },
            ],
        },
    },
    thresholds: {
        http_req_failed: ['rate<0.02'],
        http_req_duration: ['p(99)<1500'],
    },
};

export default function (data) {
    const base = __ENV.BASE_URL || 'http://localhost:8080';
    const headers = authHeaders(data.authToken);

    if (data.courseIds.length > 0) {
        const randomCourseId = pick(data.courseIds);
        apiGet(base, `/api/course-configurator/courses/${randomCourseId}/details`, headers);
    }

    sleep(0.5);
}