import http from 'k6/http';
import { check, fail } from 'k6';

export function apiGet(base, path, params = {}, expected = 200) {
    const res = http.get(`${base}${path}`, params);
    check(res, {
        [`GET ${path} -> ${expected}`]: (r) => r.status === expected,
        'p95<500ms': (r) => r.timings.duration < 500,
    }) || fail(`GET ${path} failed: ${res.status} ${res.body}`);

    return res;
}

export function apiPost(base, path, payload, params = {}, expected = 201) {
    const res = http.post(`${base}${path}`, JSON.stringify(payload), {
        headers: { 'Content-Type': 'application/json', ...(params.headers || {}) },
        ...params,
    });
    check(res, {
        [`POST ${path} -> ${expected}`]: (r) => r.status === expected,
        'p95<800ms': (r) => r.timings.duration < 800,
    }) || fail(`POST ${path} failed: ${res.status} ${res.body}`);

    return res;
}