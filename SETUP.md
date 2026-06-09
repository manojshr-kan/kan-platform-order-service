# order-service — GitHub & AWS Setup

What must be configured for this repo's CI/CD to work end to end.

## 1. GitHub repository variables

Settings → Secrets and variables → Actions → **Variables**:

| Variable | Value |
|----------|-------|
| `AWS_ROLE_ARN` | `arn:aws:iam::<account>:role/kan-cloud-platform-github-actions` |
| `AWS_REGION` | `ap-south-1` |

No secrets are needed — authentication to AWS is via OIDC.

## 2. OIDC trust (one-time, in Terraform)

This repo must be listed in the CI role's trust policy in
`kan-cloud-platform-terraform/iam-oidc.tf`:

```
repo:manojshr-kan/kan-platform-order-service:*
```

Already included. Re-apply the bootstrap layer if it was added later.

## 3. Prerequisites provisioned by Terraform

- ECR repository `order-service`
- IRSA role `order-service-irsa` (its ARN goes into the GitOps `values-dev.yaml`)
- SQS queue `order-service-queue`, SNS topic, Secrets Manager secret `order-service/config`
- Cognito user pool + `order-service` app client

## 4. CI/CD flow

```
PR opened        -> reusable java-build-test runs (mvn verify)
merge to main    -> reusable build-and-push builds image, pushes to ECR
                 -> open a PR in kan-cloud-platform-gitops bumping the image tag
                 -> Argo CD syncs the new image into EKS dev
```

## 5. Populate the secret (manual, one-time)

Terraform creates the secret empty. Set its value:

```bash
aws secretsmanager put-secret-value \
  --secret-id order-service/config \
  --secret-string '{"example.key":"example-value"}'
```

## 6. Verify in cluster

```bash
kubectl -n applications get pods
kubectl -n applications port-forward svc/order-service 8080:80
curl localhost:8080/health     # expect 200 {"status":"UP",...}
```
