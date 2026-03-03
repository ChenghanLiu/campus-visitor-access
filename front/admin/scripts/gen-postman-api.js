// scripts/gen-postman-api.js
// 用法：node scripts/gen-postman-api.js ./Lab\ Safety\ System\ Api.postman_collection.json ./src/api
// 生成：src/api/auth.js, equipment.js, reservation.js...

const fs = require("fs");
const path = require("path");

function readJson(p) {
    return JSON.parse(fs.readFileSync(p, "utf-8"));
}

function ensureDir(dir) {
    if (!fs.existsSync(dir)) fs.mkdirSync(dir, { recursive: true });
}

function sanitizeName(s) {
    return s.replace(/[^a-zA-Z0-9_]/g, "_");
}

function pickApiPath(urlObj) {
    // Postman v2.1: url can be string or object
    if (!urlObj) return null;
    if (typeof urlObj === "string") return urlObj;

    // Prefer path array
    if (urlObj.path && Array.isArray(urlObj.path)) {
        return "/" + urlObj.path.join("/");
    }
    if (urlObj.raw) {
        try {
            const u = new URL(urlObj.raw);
            return u.pathname;
        } catch {
            return urlObj.raw;
        }
    }
    return null;
}

function flattenItems(items, out = []) {
    for (const it of items || []) {
        if (it.item) flattenItems(it.item, out);
        else out.push(it);
    }
    return out;
}

function groupKeyFromPath(p) {
    // /api/equipment -> equipment
    // /api/training/courses -> training
    const seg = p.split("/").filter(Boolean);
    const apiIdx = seg.indexOf("api");
    if (apiIdx === -1 || seg.length <= apiIdx + 1) return "misc";
    return seg[apiIdx + 1];
}

function makeFuncName(method, apiPath, itemName) {
    // e.g. GET /api/equipment/{id} -> getEquipmentById
    const seg = apiPath.split("/").filter(Boolean);
    const apiIdx = seg.indexOf("api");
    const rest = apiIdx === -1 ? seg : seg.slice(apiIdx + 1);

    const base = rest
        .map(s => s.replace(/[^a-zA-Z0-9]/g, ""))
        .filter(Boolean)
        .map(s => s[0].toUpperCase() + s.slice(1))
        .join("");

    const m = method.toLowerCase();
    // fallback: use postman item name
    const n = base || sanitizeName(itemName || "api");
    return m + n;
}

function main() {
    const [,, inFile, outDir] = process.argv;
    if (!inFile || !outDir) {
        console.error("Usage: node scripts/gen-postman-api.js <postman.json> <outDir>");
        process.exit(1);
    }

    const col = readJson(inFile);
    const requests = flattenItems(col.item);

    const groups = new Map(); // key -> array
    for (const r of requests) {
        const req = r.request;
        if (!req || !req.method) continue;

        const apiPath = pickApiPath(req.url);
        if (!apiPath || !apiPath.includes("/api/")) continue; // 只生成 /api/**

        const key = groupKeyFromPath(apiPath);
        if (!groups.has(key)) groups.set(key, []);
        groups.get(key).push({
            name: r.name,
            method: req.method.toUpperCase(),
            apiPath,
        });
    }

    ensureDir(outDir);

    for (const [key, arr] of groups.entries()) {
        const lines = [];
        lines.push(`// AUTO-GENERATED from Postman collection. Edit if needed.`);
        lines.push(`import http from "@/utils/http";`);
        lines.push("");
        lines.push(`// ${key} APIs`);
        lines.push("");

        // 去重：同 method + path 只留一个
        const seen = new Set();

        for (const it of arr) {
            const sig = `${it.method} ${it.apiPath}`;
            if (seen.has(sig)) continue;
            seen.add(sig);

            const fn = makeFuncName(it.method, it.apiPath, it.name);

            // 提供 params/data 两种形态：GET 用 params，其他用 data
            if (it.method === "GET" || it.method === "DELETE") {
                lines.push(`export function ${fn}(params = {}, config = {}) {`);
                lines.push(`  return http({ url: "${it.apiPath}", method: "${it.method}", params, ...config });`);
                lines.push(`}`);
            } else {
                lines.push(`export function ${fn}(data = {}, config = {}) {`);
                lines.push(`  return http({ url: "${it.apiPath}", method: "${it.method}", data, ...config });`);
                lines.push(`}`);
            }
            lines.push("");
        }

        const out = path.join(outDir, `${key}.js`);
        fs.writeFileSync(out, lines.join("\n"), "utf-8");
        console.log("generated:", out);
    }
}

main();
