# kan-platform-order-service

A Spring Boot 3 (Java 21) service on the kan-cloud-platform. Demonstrates the
full platform pattern: containerized build, JWT auth via Cognito, secrets via
IRSA, and an SQS produce/consume loop.

## Endpoints

| Method | Path | Auth | Purpose |
|--------|------|------|---------|
| GET | `/health` | public | Liveness/readiness for the ALB |
| GET | `/actuator/health` | public | Kubernetes probes |
| POST | `/orders` | Bearer JWT | Publishes the order to SQS |

## How it fits the platform

- **Build:** `.github/workflows/ci.yml` calls the reusable workflows in
  `kan-cloud-platform-ci` — test on every PR, build+push image on merge to main.
- **Deploy:** GitOps. A promotion PR in `kan-cloud-platform-gitops` bumps the
  image tag; Argo CD syncs it into the cluster.
- **Identity:** runs as a Kubernetes service account annotated with an IRSA role
  (provisioned by Terraform) — no static AWS keys.
- **Config:** `spring-cloud-aws` resolves `order-service/config` from Secrets
  Manager at startup.
- **Auth:** validates incoming JWTs against the Cognito issuer.

## Run locally

```bash
mvn spring-boot:run
curl localhost:8080/health
```

Local runs use your AWS CLI credentials for SQS/Secrets. The Cognito issuer is
set via `COGNITO_ISSUER_URI` (leave unset locally to skip JWT validation wiring).

See `SETUP.md` for the GitHub + AWS configuration required for CI/CD.
