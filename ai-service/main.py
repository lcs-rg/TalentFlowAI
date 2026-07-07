from fastapi import FastAPI
from pydantic import BaseModel
from groq import Groq
from sentence_transformers import SentenceTransformer
import os, time, uuid, json

app = FastAPI(title="TalentFlow AI Service", version="2.0.0")

# ─── Clients ─────────────────────────────────────────
groq_client = Groq(api_key=os.getenv("GROQ_API_KEY"))
chat_model = os.getenv("GROQ_MODEL", "mixtral-8x7b-32768")

# Embeddings model (loaded once at startup, runs on CPU by default)
embed_model = SentenceTransformer(
    os.getenv("EMBEDDING_MODEL", "all-MiniLM-L6-v2"),
    device="cpu"
)
EMBEDDING_DIM = embed_model.get_sentence_embedding_dimension()  # 384

# ─── Schemas ──────────────────────────────────────────

class ParseResumeRequest(BaseModel):
    resume_text: str
    request_id: str | None = None

class MatchRequest(BaseModel):
    job_description: str
    candidate_resume: str
    request_id: str | None = None

class EmbedRequest(BaseModel):
    text: str
    request_id: str | None = None

class EmbedResponse(BaseModel):
    success: bool
    embedding: list[float] | None = None
    dimensions: int
    model: str
    error: dict | None = None

class AIResponse(BaseModel):
    success: bool
    data: dict | None = None
    error: dict | None = None
    metadata: dict

# ─── Helpers ──────────────────────────────────────────

def load_prompt(name: str) -> str:
    with open(f"prompts/{name}", encoding="utf-8") as f:
        return f.read()

def audit_meta(model: str, start: float, prompt_version: str,
               tokens: int | None, request_id: str | None) -> dict:
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
        resp = groq_client.chat.completions.create(
            model=chat_model,
            messages=[{"role": "system", "content": prompt}],
            response_format={"type": "json_object"},
            temperature=0.1
        )
        data = json.loads(resp.choices[0].message.content)
        tokens = resp.usage.total_tokens if resp.usage else None
        return AIResponse(success=True, data=data,
                          metadata=audit_meta(chat_model, start, "v2.0", tokens, req.request_id))
    except Exception as e:
        return AIResponse(success=False,
                          error={"code": "AI_PARSE_ERROR", "message": str(e)},
                          metadata=audit_meta(chat_model, start, "v2.0", None, req.request_id))


@app.post("/match")
async def match(req: MatchRequest):
    start = time.time()
    try:
        prompt = (load_prompt("job-candidate-match.txt")
                  .replace("{{job_description}}", req.job_description)
                  .replace("{{candidate_resume}}", req.candidate_resume))
        resp = groq_client.chat.completions.create(
            model=chat_model,
            messages=[{"role": "system", "content": prompt}],
            response_format={"type": "json_object"},
            temperature=0.1
        )
        data = json.loads(resp.choices[0].message.content)
        tokens = resp.usage.total_tokens if resp.usage else None
        return AIResponse(success=True, data=data,
                          metadata=audit_meta(chat_model, start, "v2.0", tokens, req.request_id))
    except Exception as e:
        return AIResponse(success=False,
                          error={"code": "AI_MATCH_ERROR", "message": str(e)},
                          metadata=audit_meta(chat_model, start, "v2.0", None, req.request_id))


@app.post("/embed", response_model=EmbedResponse)
async def embed(req: EmbedRequest):
    """Generate embedding vector for semantic search."""
    try:
        vector = embed_model.encode(req.text).tolist()
        return EmbedResponse(
            success=True,
            embedding=vector,
            dimensions=EMBEDDING_DIM,
            model=os.getenv("EMBEDDING_MODEL", "all-MiniLM-L6-v2"),
            error=None
        )
    except Exception as e:
        return EmbedResponse(
            success=False,
            embedding=None,
            dimensions=EMBEDDING_DIM,
            model=os.getenv("EMBEDDING_MODEL", "all-MiniLM-L6-v2"),
            error={"code": "EMBED_ERROR", "message": str(e)}
        )


@app.get("/health")
async def health():
    return {
        "status": "healthy",
        "service": "talentflow-ai",
        "version": "2.0.0",
        "chat_model": chat_model,
        "embedding_model": os.getenv("EMBEDDING_MODEL", "all-MiniLM-L6-v2"),
        "embedding_dim": EMBEDDING_DIM
    }
