# Security First

Sempre que implementar uma funcionalidade, execute mentalmente este checklist:

- Existe SQL Injection?
- Existe XSS?
- Existe CSRF?
- Existe Broken Access Control?
- Existe IDOR (Insecure Direct Object Reference)?
- Existe Path Traversal?
- Existe File Upload inseguro?
- Existe exposição de informações sensíveis?
- Existe possibilidade de enumeração de usuários?
- Existe Rate Limit?
- Existe abuso da IA?

Caso qualquer resposta seja positiva, interrompa a implementação e proponha uma solução segura antes de continuar.
