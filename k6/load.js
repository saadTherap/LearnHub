import { sleep } from 'k6';
import { apiGet } from './lib/httpClient.js';
import { login, authHeaders, pick } from './lib/helpers.js';

let authToken;
let courseIds = [];

// Get data and auth token before the test starts
export function setup() {
    const base = __ENV.BASE_URL || 'http://localhost:8080';
    const token = login();
    const headers = authHeaders(token);
    const coursesRes = apiGet(base, '/api/course-configurator/public/courses', headers);
    courseIds = coursesRes.json().map(c => c.id);

    return { authToken: token, courseIds: courseIds };
}

export const options = {
    scenarios: {
        steady_load: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '10s', target: 1000 },
                { duration: '20s', target: 1000 },
                { duration: '10s', target: 0 },
            ],
            gracefulRampDown: '30s',
        },
    },
    thresholds: {
        http_req_failed: ['rate<0.01'],
        http_req_duration: ['p(95)<800'],
    },
};

export default function (data) {
    const base = __ENV.BASE_URL || 'http://localhost:8080';
    const headers = authHeaders(data.authToken);
    // console.log(headers);

    // authenticated user gets a random course detail
    // if (data.courseIds.length > 0) {
    //     const randomCourseId = pick(data.courseIds);
    //     apiGet(base, `/api/course-configurator/courses/${randomCourseId}/details`, headers);
    // } else {
        // Fallback if no courses were found in setup
        apiGet(base, '/api/course-configurator/public/courses', headers);
    // }

    sleep(Math.random() * 2);
}