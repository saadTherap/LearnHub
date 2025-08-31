import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '30s', target: 10 },  // Stage 1: Ramp up to 10 virtual users over 30 seconds
        { duration: '1m', target: 20 }, // Stage 2: Stay at 20 virtual users for 1 minute (simulating steady load)
        { duration: '30s', target: 0 },  // Stage 3: Ramp down to 0 virtual users over 30 seconds
    ],

    // The test will fail if any of these conditions are not met
    thresholds: {
        http_req_duration: ['p(95)<500'], // 95% of requests must complete in under 500ms
        http_req_failed: ['rate<0.01'],    // The failure rate must be below 1%
        'checks': ['rate>0.99'],          // 99% of checks (assertions) must pass
    },
};

export default function () {
    // Send a GET request to the /courses endpoint
    const res = http.get('https://app-rnd01.therapdev.net/api/course-configurator/public/courses');

    // Check if the response status is 200 OK
    check(res, {
        'is status 200': (r) => r.status === 200,
    });

    // Pause for a short period to simulate user behavior
    sleep(1);
}