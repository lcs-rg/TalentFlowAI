from fastapi import FastAPI
from pydantic import BaseModel
from openai import OpenAI
import os, time, uuid, json

app = FastAPI(title="TalentFlow AI Service", version="1.0.0")
client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

# ─── Schemas ──────────────────────────────────────────

class ParseResumeRequest(BaseModel):
    resume_text: str
    request_id: str | None = None

class MatchRequest(BaseModel):
    job_description: str
    candidate_resume: str
    request_id: str | None = None

class AIResponse(BaseModel):
    success: bool
    data: dict | None = None
    error: dict | None = None
    metadata: dict

def load_prompt(name: str) -> str:
    with open(f"prompts/{name}", encoding="utf-8") as f:
        return f.read()

def audit_meta(model: str, start: float, prompt_version: str, tokens: int | None, request_id: str | None) -> dict:
    return {
        "model": model,
        "prompt_version": prompt_version,
        "duration_ms": int((time.time() - start) * 1000),
        "tokens_used": tokens,
        "request_id": request_id or str(uuid.uuid4())
    }

# ─── Endpoints ────────────────────────────────────────

@app.post("/parse-resume")
async def parse_resume(req: ParseResumeRequest):
    start = time.time()
    try:
        prompt = load_prompt("resume-parser.txt").replace("{{resume_text}}", req.resume_text)
        resp = client.chat.completions.create(
            model="gpt-4o-mini",
            messages=[{"role": "system", "content": prompt}],
            response_format={"type": "json_object"},
            temperature=0.1
        )
        data = json.loads(resp.choices[0].message.content)
        return AIResponse(success=True, data=data, metadata=audit_meta("gpt-4o-mini", start, "v1.0", resp.usage.total_tokens if resp.usage else None, req.request_id))
    except Exception as e:
        return AIResponse(success=False, error={"code": "AI_PARSE_ERROR", "message": str(e)}, metadata=audit_meta("gpt-4o-mini", start, "v1.0", None, req.request_id))

@app.post("/match")
async def match(req: MatchRequest):
    start = time.time()
    try:
        prompt = load_prompt("job-candidate-match.txt").replace("{{job_description}}", req.job_description).replace("{{candidate_resume}}", req.candidate_resume)
        resp = client.chat.completions.create(
            model="gpt-4o-mini",
            messages=[{"role": "system", "content": prompt}],
            response_format={"type": "json_object"},
            temperature=0.1
        )
        data = json.loads(resp.choices[0].message.content)
        return AIResponse(success=True, data=data, metadata=audit_meta("gpt-4o-mini", start, "v1.0", resp.usage.total_tokens if resp.usage else None, req.request_id))
    except Exception as e:
        return AIResponse(success=False, error={"code": "AI_MATCH_ERROR", "message": str(e)}, metadata=audit_meta("gpt-4o-mini", start, "v1.0", None, req.request_id))

@app.get("/health")
async def health():
    return {"status": "healthy", "service": "talentflow-ai"}
