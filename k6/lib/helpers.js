import { check } from 'k6';
import exec from 'k6/execution';
import http from 'k6/http';

export function pick(arr) { return arr[Math.floor(Math.random() * arr.length)]; }

export function authHeaders(token) {
    return token ? { headers: { Authorization: `Bearer ${token}` } } : {};
}

export function tag(name, value) {
    exec.vu.tags[name] = value;
}

export function json(res) {
    try { return res.json(); } catch { return null; }
}

// Login helper function, assuming a /auth/login endpoint
export function login() {
    const res = http.post(`${__ENV.BASE_URL}/auth/api/login`, JSON.stringify({
        email: __ENV.AUTH_USER,
        password: __ENV.AUTH_PASS,
    }), { headers: { 'Content-Type': 'application/json' }});
    check(res, { 'login successful': (r) => r.status === 200 });

    return res.json()?.token;
}