import fs from "fs";
import fetch from "node-fetch";

const diff = fs.readFileSync("diff.txt", "utf8");

if (!diff.trim()) {
  console.log("No diff found, skipping review.");
  process.exit(0);
}

const prompt = `
You are a senior software engineer.

Review the following GitHub Pull Request diff.

Focus on:
- Bugs
- Security issues
- Performance
- Code style
- Architecture problems

Reply in Turkish.
Use bullet points.
Be strict but constructive.

DIFF:
${diff}
`;

const response = await fetch("https://openrouter.ai/api/v1/chat/completions", {
  method: "POST",
  headers: {
    "Authorization": \`Bearer \${process.env.OPENROUTER_API_KEY}\`,
    "Content-Type": "application/json",
    "HTTP-Referer": "https://github.com",
    "X-Title": "AI Code Review Bot"
  },
  body: JSON.stringify({
    model: "anthropic/claude-3.5-sonnet",
    messages: [{ role: "user", content: prompt }],
    temperature: 0.2
  })
});

const data = await response.json();
const review = data.choices?.[0]?.message?.content || "AI review failed.";

// PR numarasını bul
const ref = process.env.GITHUB_REF;
const match = ref.match(/pull\/(\\d+)/);
if (!match) {
  console.error("PR number not found.");
  process.exit(1);
}

const prNumber = match[1];
const repo = process.env.GITHUB_REPOSITORY;

// PR'a comment at
await fetch(`https://api.github.com/repos/${repo}/issues/${prNumber}/comments`, {
  method: "POST",
  headers: {
    "Authorization": \`Bearer \${process.env.GITHUB_TOKEN}\`,
    "Content-Type": "application/json"
  },
  body: JSON.stringify({ body: review })
});

console.log("AI review comment posted.");
