# Context-Governed Engineering (CGE)

## Quick Start and Bootstrap Guide

**This section provides practical bootstrap instructions for using this CGE repository. For complete specifications and detailed explanations, see the full document set below.**

**New to CGE? Start with the comprehensive guide:** [Project Initialization and Execution](docs/workflows/project_initialization_and_execution.md)

This guide explains:
- How CGE's two-repository model works (framework vs. project)
- The critical `PROJECT_PATH` variable and where `.cge` directories get created
- Complete step-by-step workflows for new projects, modernization, and adding features
- Troubleshooting and verification

**Quick example:**
```bash
# Always set PROJECT_PATH to tell CGE where your project lives
export PROJECT_PATH="/home/coder/set4d/apps/my-service"

# Run all workflows FROM the CGE framework directory
cd /home/coder/set4d/cge

# CGE will create .cge/ directory at ${PROJECT_PATH}/.cge/
bash runners/workflows/initialize-springboot-project.sh \
  --target-path ${PROJECT_PATH} \
  --service-name my-service
```

### What is this repository?

This is the **CGE docs repository** for the set4d environment. It contains:
- Global context (architecture patterns, best practices, security standards, governance rules)
- Task prompt templates (32 prompts for analysis, design, implementation, testing)
- Workflow definitions (22 YAML workflows orchestrating bounded tasks)
- JSON schemas (27 schemas for artifacts, audits, and reports)
- Runner scripts (shell orchestration and execution engines)
- Example artifacts and end-to-end workflows

### Prerequisites

Before using CGE, verify you have these tools installed:

**REQUIRED:**
- Python 3 with PyYAML: `pip3 install PyYAML`
- jq (JSON parser): `sudo apt-get install jq` or `brew install jq`
- Claude CLI: Install from https://claude.com/claude-code
- Git (for provenance tracking)

**For building generated code:**
- Maven (Spring Boot projects): `sudo apt-get install maven`

**OPTIONAL:**
- yq v4+ (Mike Farah's version) - CGE uses Python yaml-query.py if yq is unavailable

**Verify prerequisites:**
```bash
cd /path/to/cge
bash test-setup.sh
```

See [PREREQUISITES.md](PREREQUISITES.md) for detailed installation instructions.

### Repository Structure

```
cge/                              # CGE docs repository root
├── README.md                     # This file (specification + guide)
├── CLAUDE.md                     # Instructions for Claude Code
├── PREREQUISITES.md              # Installation requirements
├── test-setup.sh                 # Prerequisites verification script
├── global_context/               # Authoritative governance (read before making changes)
│   ├── patterns/                 # Architecture patterns
│   ├── best_practices/           # Engineering best practices
│   ├── security/                 # Security standards
│   ├── auditing/                 # Audit requirements
│   └── governance/               # Governance model
├── prompts/                      # Task prompt templates
│   └── tasks/                    # 32 prompts: analysis.*.md, design.*.md, implementation.*.md, testing.*.md
├── workflows/                    # Workflow definitions
│   └── definitions/              # 22 YAML workflows organized by phase
│       ├── analysis/             # C++ decomposition, file/class inventory, algorithm extraction
│       ├── design/               # Feature decomposition, planning, test strategy
│       ├── implementation/       # Work plan execution, Spring Boot implementation
│       └── testing/              # Test extraction and verification
├── schemas/                      # JSON schemas for validation
│   ├── artifact/                 # ASA schemas (project_context, work_plan, etc.)
│   ├── audit/                    # Audit schemas (task_audit, vibe_session_audit)
│   ├── cpp/                      # C++ decomposition schemas
│   ├── springboot/               # Spring Boot API/DTO schemas
│   ├── modernization/            # Modernization analysis schemas
│   ├── verification/             # Test and tolerance schemas
│   ├── task/                     # Task checkpoint schema
│   ├── reporting/                # Report schemas
│   └── registry.json             # Schema catalog
├── runners/                      # Execution engine
│   ├── bin/                      # Core runner scripts
│   │   ├── cge-run.sh            # Main manifest-driven workflow runner
│   │   ├── cge-task.sh           # Single task executor
│   │   ├── cge-validate.sh       # Schema validation gates
│   │   ├── cge-report.sh         # Run report and drift analysis generator
│   │   ├── cge-checkpoint.sh     # Progress tracking and resumability
│   │   ├── cge-resume.sh         # Resume failed tasks
│   │   ├── yaml-query.py         # Python YAML parser (yq alternative)
│   │   └── validate-*.sh         # Validation scripts
│   ├── workflows/                # Convenience wrapper scripts (23 wrappers)
│   │   ├── cpp-decomposition.sh  # C++ analysis workflows (unified CLI)
│   │   ├── define-project-context.sh
│   │   ├── decompose-feature.sh
│   │   ├── plan-service-or-feature.sh
│   │   ├── execute-work-plan.sh
│   │   └── decompose-*.sh        # Various decomposition workflows
│   └── manifests/                # Ready-to-use workflow manifests (if any)
├── examples/                     # Example artifacts and workflows
│   ├── end_to_end/               # End-to-end workflow examples
│   ├── cpp_decomposition/        # C++ decomposition examples
│   ├── api_decomposition/        # Spring Boot API examples
│   ├── modernization/            # Modernization examples
│   ├── verification/             # Test verification examples
│   └── vibe_session/             # Vibe session audit examples
├── docs/                         # Additional documentation
│   └── vibe_sessions.md          # Vibe session mode guide
└── reporting/                    # Reporting infrastructure
    ├── definitions/
    └── drift_rules/
```

### Key Concepts (Brief)

- **Bounded Task**: Atomic unit of work limited to exactly one conceptual boundary (e.g., "controller", "service", "cpp_algorithms")
- **Workflow**: Orchestrates multiple bounded tasks to achieve an SDLC outcome
- **Authoritative Structured Artifact (ASA)**: Canonical JSON output validated against schemas
- **Audit**: Mandatory record of task execution with provenance, assumptions, risks, confidence
- **Global Context**: Authoritative governance rules in `global_context/` (always read before making changes)
- **Gates**: Validation checkpoints (schema validation, boundary compliance)
- **Run**: A workflow execution with unique ID `{YYYYMMDD-NNN}/{phase}/{label}`, stored in `.cge/runs/`

### Common Workflows

CGE supports multiple workflow types:

#### 1. C++ Legacy Code Decomposition (Analysis Phase)

Extract structure and algorithms from legacy C++ codebases:

```bash
# Full decomposition (file inventory, class inventory, algorithms, build system)
bash runners/workflows/cpp-decomposition.sh complete \
  --cpp-project /path/to/cpp/code

# File classification only
bash runners/workflows/cpp-decomposition.sh file-inventory \
  --cpp-project /path/to/cpp/code

# Class structure extraction
bash runners/workflows/cpp-decomposition.sh class-basic-inventory \
  --cpp-project /path/to/cpp/code

# Algorithm extraction
bash runners/workflows/cpp-decomposition.sh algorithms \
  --cpp-project /path/to/cpp/code

# Interactive menu
bash runners/workflows/cpp-decomposition.sh menu
```

Outputs: `.cge/runs/{run-id}/artifacts/` with JSON files for file inventory, class inventory, algorithms, build system.

#### 2. Spring Boot Feature Implementation (Design + Implementation Phases)

Build new Spring Boot services or features through 4-phase workflow:

```bash
# Phase 1: Define project context (analysis)
bash runners/workflows/define-project-context.sh \
  --project-path /path/to/service \
  --project-name my-service \
  --requirements-ref /path/to/feature-request.md

# Phase 2: Decompose feature (design)
bash runners/workflows/decompose-feature.sh \
  --project-path /path/to/service \
  --feature-request /path/to/feature-request.md \
  --project-context-ref .cge/runs/.../project_context.json

# Phase 3: Plan implementation (design)
bash runners/workflows/plan-service-or-feature.sh \
  --project-path /path/to/service \
  --project-context-ref .cge/runs/.../project_context.json \
  --decomposition-ref .cge/runs/.../decomposition_report.json

# Phase 4: Execute plan (implementation)
bash runners/workflows/execute-work-plan.sh \
  --project-path /path/to/service \
  --work-plan-ref .cge/runs/.../work_plan.json
```

Outputs: Spring Boot source code (src/main/java, src/test/java, pom.xml, application.yml) plus ASAs and audits.

#### 3. Spring Boot API Documentation

Extract API contracts and DTOs from existing Spring Boot apps:

```bash
bash runners/workflows/decompose-production-api.sh \
  --springboot-codebase /path/to/spring/boot/app \
  --project-path /tmp/work
```

Outputs: API contract and DTO contract JSON artifacts.

### Output Structure

All workflow executions create structured outputs in `.cge/runs/`:

```
.cge/runs/{YYYYMMDD-NNN}/{phase}/{label}/
├── manifest.json              # Run metadata
├── prompts/                   # Resolved prompts sent to Claude
│   └── {task-id}.resolved.md
├── checkpoints/               # Task progress tracking
│   └── {task-id}.checkpoint.json
├── audits/                    # Task audit records (one per task)
│   ├── {task-id}.audit.json
│   └── vibe-session-*.audit.json  # Optional vibe session audits
├── artifacts/                 # Canonical JSON outputs
│   ├── {artifact-type}/
│   │   └── {artifact-name}.json
│   └── ...
├── logs/                      # Execution logs and metrics
│   └── {task-id}.metrics.json  # Cost, tokens, turns
├── gates/                     # Validation results
│   └── {gate-id}.outcome.json
└── reports/                   # Aggregated reports
    ├── run_report.json        # Cost summary, findings
    └── drift_report.json      # Pattern analysis
```

### Validation and Reporting

**Validate artifacts:**
```bash
bash runners/bin/cge-validate.sh \
  --schema artifact.project_context@1.0.0 \
  --artifact .cge/runs/{run-id}/artifacts/project_context/project_context.json
```

**Generate reports:**
```bash
export RUN_DIR=".cge/runs/{run-id}"
export REPORT_TYPE="run_report"  # or "drift_analysis"
bash runners/bin/cge-report.sh
```

**Resume failed tasks:**
```bash
bash runners/bin/cge-resume.sh \
  .cge/runs/{run-id} \
  {task-id}
```

### End-to-End Example: context-service

**Setup:**
```bash
# Directory structure
Projects/
  cge/              # This repository
  context-service/  # Your project

cd context-service

# Create seed files
mkdir -p .cge
cat > .cge/feature-request.md << 'EOF'
Create a new Spring Boot Context Service from scratch.

The service must:
- Expose GET /api/v1/contexts/{id}
- Expose POST /api/v1/contexts
- All endpoints require ROLE_USER authorization
- Use WebClient to call external PreferencesService
- Include Micrometer counter: context.fetch.count
- Maven project, Spring Boot 3.3.x
- Unit tests (no integration tests)
EOF

cat > .cursorrules << 'EOF'
# CGE-Governed Project
This project is governed by CGE global context at ../cge/global_context/
Read all files under ../cge/global_context/ before making changes.
EOF
```

**Run workflow:**
```bash
# All phases in sequence
CGE_ROOT="../cge"
PROJECT_PATH="."
FEATURE_REQUEST=".cge/feature-request.md"

# Phase 1
bash "${CGE_ROOT}/runners/workflows/define-project-context.sh" \
  --project-path "${PROJECT_PATH}" \
  --project-name context-service \
  --requirements-ref "${FEATURE_REQUEST}"

# Get artifact path
CTX=$(find .cge/runs/analysis -name "project_context.json" | sort | tail -1)

# Phase 2
bash "${CGE_ROOT}/runners/workflows/decompose-feature.sh" \
  --project-path "${PROJECT_PATH}" \
  --feature-request "${FEATURE_REQUEST}" \
  --project-context-ref "${CTX}"

DECOMP=$(find .cge/runs/design -name "decomposition_report.json" | sort | tail -1)

# Phase 3
bash "${CGE_ROOT}/runners/workflows/plan-service-or-feature.sh" \
  --project-path "${PROJECT_PATH}" \
  --project-context-ref "${CTX}" \
  --decomposition-ref "${DECOMP}"

PLAN=$(find .cge/runs/design -name "work_plan.json" | sort | tail -1)

# Phase 4
bash "${CGE_ROOT}/runners/workflows/execute-work-plan.sh" \
  --project-path "${PROJECT_PATH}" \
  --work-plan-ref "${PLAN}"
```

**Expected outputs:**
- Source code: `src/main/java/com/set4d/context/` with controller, service, DTOs, config, tests
- Build files: `pom.xml`, `src/main/resources/application.yml`
- Artifacts: `project_context.json`, `decomposition_report.json`, `work_plan.json`
- Audits: One audit per task (8 total audits)
- Reports: `run_report.json` with cost summary and findings

**Build and test:**
```bash
cd context-service
mvn clean test
mvn spring-boot:run
```

### Important Notes

1. **Read global_context/ first**: Always read files in `global_context/` before making changes. These are authoritative governance rules.

2. **Bounded tasks prevent scope creep**: Each task has strict file path boundaries and conceptual boundaries to prevent layer violations.

3. **Audits are mandatory**: Every task MUST produce an audit file at the exact specified path.

4. **Schema validation**: All artifacts are validated against JSON schemas before workflow completes.

5. **Cost tracking**: All task executions track cost (USD), tokens (input/output/cache), and turns. View in `.cge/runs/{run-id}/logs/*.metrics.json` or aggregated in `run_report.json`.

6. **Resumability**: Long-running tasks create checkpoints. Failed tasks can be resumed without losing progress.

7. **Vibe sessions**: For exploratory artifact review, use vibe session mode (see `docs/vibe_sessions.md`).

### Key Artifacts

CGE workflows produce schema-validated JSON artifacts. Key artifacts include:

#### Project Context Artifact
**Schema:** `artifact.project_context@1.0.0`
**Generated by:** `define-project-context` workflow
**Purpose:** Defines project scope, type, components, and constraints

Contains:
- Project name and type (spring_boot_app, microservice, library)
- Components and their descriptions
- Applicable boundaries (controller, service, repository, etc.)
- Technology stack and architectural patterns
- Performance requirements

**Use Cases:** Input to all subsequent workflows, establishes domain vocabulary

#### Decomposition Report Artifact
**Schema:** `artifact.decomposition_report@1.0.0`
**Generated by:** `decompose-feature` workflow
**Purpose:** Feature breakdown into bounded components with dependencies

Contains:
- Feature overview and scope
- Component inventory with boundaries and responsibilities
- Dependency graph between components
- Data flow and integration points

**Use Cases:** Drives work plan creation, validates feature completeness

#### Work Plan Artifact
**Schema:** `artifact.work_plan@1.0.0`
**Generated by:** `plan-service-or-feature` workflow
**Purpose:** Sequenced implementation tasks with dependencies

Contains:
- Ordered task list with IDs and boundaries
- Prompt references for each task
- File path boundaries (allowed/disallowed paths)
- Dependencies between tasks
- Model selection and turn budgets

**Use Cases:** Input to `execute-work-plan` workflow for implementation

#### Project Structure Artifact
**Schema:** `artifact.project_structure@1.0.0`
**Generated by:** `document-project-structure` task (post-implementation)
**Purpose:** Complete inventory of generated code structure for design reviews and onboarding

Contains:
- Package-by-package class inventory with types and dependencies
- Architecture pattern validation (layer separation enforcement)
- Test structure and coverage mapping
- Infrastructure files (Docker, CI/CD, config)
- Component inventory mapped to conceptual boundaries

**Use Cases:**
- New developer onboarding (understand code organization)
- Architecture audits (verify layer separation)
- Drift detection (compare expected vs actual structure)
- Documentation generation (auto-generate diagrams)

**Example Usage:**
```bash
# Generate project structure artifact after implementation
bash runners/bin/cge-task.sh \
  --task-id "document-project-structure" \
  --boundary "project_structure" \
  --prompt-ref "prompts/tasks/analysis.project_structure.document-project-structure.md" \
  --model "haiku" \
  --max-turns 30 \
  --env PROJECT_PATH=/path/to/project \
  --env RUN_ID=$(date +%Y%m%d-%s)
```

### Document Organization

This README contains multiple documents:

- **This Quick Start** (above): Practical bootstrap instructions
- **Document 0**: Executive Summary (CGE overview, concepts, value)
- **Document 1**: White Paper (detailed rationale and benefits)
- **Document 2**: Specification (normative requirements)
- **Document 3**: Implementation Guide (detailed execution model, runner internals)
- **Annexes A-H**: Taxonomies, schemas, profiles, multi-source decomposition

For initial CGE setup, focus on this Quick Start and Document 3 (Implementation Guide, starting at line ~788).

For understanding CGE philosophy and governance, read Documents 0-2.

For deep technical reference, use Document 3 and the annexes.

---

## Document Set v1.0 Draft, Revised for Production Readiness and Distribution

---

# Document 0: Executive Summary

## Context-Governed Engineering (CGE)

### Executive Summary v1.0

## Authority

This document is informational. It summarizes the CGE operating model for leadership, architects, platform teams, and engineering organizations evaluating adoption.

## What CGE is

Context-Governed Engineering (CGE) is an engineering operating model for governed SDLC execution. It uses structured context, bounded tasks, canonical artifacts, audits, gates, reporting, and actionable outputs to make engineering work more consistent, traceable, reproducible, and improvable, whether AI is used or not.

AI is an execution surface within CGE, not an authority outside it. Authority resides in governed context, schemas, taxonomies, gates, audits, approved profiles, and human oversight.

## Why this exists

Engineering organizations are adopting AI quickly, but adoption is often informal. Informal execution produces inconsistent outputs, architectural drift, weak provenance, unclear accountability, higher review cost, and fragmented governance.

CGE addresses this by making engineering execution structured, bounded, reviewable, reproducible, and measurable across the SDLC.

## What CGE changes

CGE changes engineering execution in five important ways:

1. Work is performed through bounded tasks rather than ad hoc requests.
2. Context is governed and treated as an authoritative input.
3. Authoritative structured outputs are captured as canonical JSON artifacts.
4. Every task produces an audit record for traceability, review, and attribution.
5. Reporting and drift signals turn execution evidence into system improvement.

## Core concepts

### Governed context

Governed context is the authoritative source of rules, patterns, vocabulary, constraints, taxonomies, and schemas.

### Bounded tasks

A task is an atomic unit of work bounded to exactly one conceptual boundary appropriate to its SDLC phase.

### Workflows

A workflow orchestrates bounded tasks to achieve an SDLC outcome.

### Authoritative Structured Artifacts

Authoritative Structured Artifacts, or ASAs, are authoritative structured outputs stored in canonical JSON for validation, reporting, traceability, and reproducibility.

### Audits

Every task emits an audit record that captures what was done, which context applied, what inputs and outputs were involved, what risks and assumptions remained, what gates were evaluated, and what confidence was present.

### Reporting and drift

Reporting rolls up artifacts, audits, and change signals into actionable visibility. Drift signals identify repeated assumptions, repeated risks, low-confidence clusters, policy conflicts, gate failures, and other indicators that the operating model should be refined.

## What changes for teams

For engineers and technical leads:

* work becomes more structured and repeatable
* implementation scope becomes easier to bound and review
* architecture rules become easier to operationalize and enforce
* outputs become easier to trace and reproduce

For architects and governance leads:

* standards become easier to operationalize
* exceptions become easier to track and review
* drift and rework become easier to detect and prioritize

For engineering leadership:

* reporting provides visibility into system health, conformance, and improvement priorities
* actionable outputs translate engineering signals into Features and Stories without coupling governance to a specific vendor tool

## Example of CGE in practice

A new service feature does not begin with an open-ended prompt. Instead, the work is decomposed into bounded tasks such as defining domain context, decomposing the feature, planning implementation, implementing a controller, implementing a service, implementing repository changes, and producing corresponding test evidence. Each step records the governing context, outputs canonical artifacts, emits an audit, and passes through gates. The result is more reviewable work, clearer provenance, and lower architectural drift.

## High-level benefits

CGE is designed to deliver:

* more consistent engineering execution across repositories and teams
* stronger architectural discipline
* lower review burden and rework
* better traceability and reproducibility
* faster onboarding through governed context and shared task patterns
* continuous improvement driven by evidence rather than anecdote

## Who uses CGE

* Engineers and leads use CGE to execute bounded work consistently.
* Architects and governance owners use CGE to define and enforce standards.
* Platform and DevOps teams use CGE to implement runners, gates, and profiles.
* Engineering leaders use CGE reporting and actionable outputs to guide improvement.

## Adoption path

1. Establish the governance model, canonical taxonomies, schema catalog, and audit requirements.
2. Pilot CGE in a small number of repositories and workflows.
3. Introduce reporting and drift review as a regular governance practice.
4. Expand the task catalogue, workflow library, and approved profile set.
5. Standardize conformance, evidence retention, and governance across the organization.

## Summary

CGE is not only a way to use AI more effectively. It is an operating model for governed SDLC execution. It makes work more bounded, evidence-based, reproducible, reviewable, and improvable while keeping human oversight and architectural integrity intact.

---

# Document 1: White Paper

## Context-Governed Engineering (CGE)

### A Technical White Paper on a Governed Operating Model for AI-Assisted and Non-AI SDLC Execution

## Authority

This document is explanatory and persuasive. It describes the conceptual model and rationale for CGE but is not normative.

## Executive Summary

Context-Governed Engineering (CGE) is an engineering operating model for governed SDLC execution. It uses governed context, bounded tasks, authoritative structured artifacts, audits, gates, reporting, and actionable outputs to make engineering work more consistent, traceable, reproducible, and improvable.

CGE supports AI-assisted execution without allowing AI to bypass architecture discipline, reviewability, or governance. It also stands on its own without AI. AI is one execution surface within the operating model, not the source of truth.

## 1. Introduction

AI assistants can accelerate engineering work, but informal adoption creates inconsistent outcomes and operational risk. Many teams treat AI as a conversational helper rather than as a governed participant in the SDLC. That creates unpredictable changes, weak provenance, and unnecessary review overhead.

CGE reframes the problem. Instead of treating prompts as the center of the system, CGE treats governed context, bounded execution, authoritative structured outputs, audits, and reporting as the center of the system.

## 2. The problem with informal AI adoption

Informal AI use produces predictable failure modes:

* **Inconsistency**: different engineers produce different structures and patterns for the same kind of work
* **Architectural drift**: generated changes may bypass layering and dependency rules
* **Poor reproducibility**: teams cannot recreate the conditions that produced an output
* **Weak traceability**: assumptions, risks, and applied rules are not captured in durable evidence
* **Unbounded scope**: prompts encourage changes that cross concerns and inflate review cost
* **Low governance visibility**: leaders and architects lack reliable signals about system quality and drift

These problems compound in multi-repository environments where standards, repeatability, and organization-level visibility matter.

## 3. What CGE is and is not

### 3.1 What CGE is

CGE is an engineering operating model that governs execution through:

* structured and governed context
* bounded tasks aligned to conceptual boundaries
* workflows aligned to SDLC phases
* canonical JSON ASAs for authoritative outputs
* mandatory audits for each task
* gates for validation and compliance
* reporting and drift signals for continuous improvement
* actionable outputs for follow-up work planning

### 3.2 What CGE is not

CGE is not:

* just a prompt library
* just a standards repository
* just CI gate enforcement
* just architecture review
* just AI tooling guidance
* a replacement for engineers, architects, or human judgment

CGE integrates these ideas into a single operating model for governed execution.

## 4. Core premise

CGE is built on a simple premise:

**Engineering work is best performed when rules and intent are explicit, bounded, validated, attributable, and observable.**

This premise applies whether the work is done manually, with scripts, or with AI assistance.

## 5. Why boundedness is central

Boundedness is the principal control primitive in CGE. A bounded task limits the conceptual surface, file modification surface, inputs, outputs, and review obligation for a unit of work.

This matters because boundedness:

* reduces scope inflation and uncontrolled side effects
* makes architecture rules enforceable at execution time
* improves provenance by associating one task to one declared boundary
* lowers review cost by shrinking the change surface
* improves reproducibility because inputs and outputs are easier to isolate
* supports safer automation because the allowable behavior is constrained

Without boundedness, context and audits become descriptive rather than governing.

## 6. Core concepts

### 6.1 Governed context

Governed context is the authoritative source of rules, vocabulary, patterns, constraints, taxonomies, and schemas that govern execution.

### 6.2 Bounded tasks

A task is an atomic unit of work bounded to exactly one conceptual boundary appropriate to its SDLC phase.

Examples:

* in analysis, a task may be bounded to responsibilities, data flow, or bounded context
* in implementation, a task may be bounded to controller, service, repository, or observability

### 6.3 Workflows

A workflow orchestrates bounded tasks to achieve a phase-appropriate outcome.

### 6.4 Authoritative Structured Artifacts

ASAs are authoritative structured outputs stored in canonical JSON. They support validation, automation, reporting, and reproducibility.

### 6.5 Audits

Every task produces an audit that records:

* what was done
* which governed context applied
* which prompts or instructions were used
* what inputs and outputs were involved
* what assumptions and risks remained
* what gates were evaluated
* what level of confidence was present

### 6.6 Reporting and drift

Reporting aggregates audits and ASAs into views suitable for engineering teams, governance bodies, and leadership. Drift signals are derived from those records and reveal where the operating model should be refined.

## 7. Control model

CGE uses a layered control model:

1. **Governed context** defines authority and allowable behavior.
2. **Canonical taxonomies and schemas** define normalized terms and structure.
3. **Tasks** bound execution scope.
4. **Runners and execution surfaces** perform work under bounded instructions.
5. **Gates** validate artifacts and outcomes.
6. **Audits** preserve provenance and uncertainty.
7. **Reporting and drift analysis** identify systemic issues.
8. **Governance** updates rules, taxonomies, workflows, and exceptions.

This model keeps AI subordinate to the operating model rather than allowing it to operate outside policy.

## 8. CGE across the SDLC

CGE aligns execution to the SDLC:

* **Analysis**: produces structured decomposition and understanding artifacts
* **Design**: produces contracts, decisions, and design evidence
* **Implementation**: performs bounded code changes under controlled workflows
* **Testing**: produces test strategy, test assets, and test evidence
* **Release and Operations**: produces readiness, operational, and observability artifacts

Each phase uses phase-appropriate conceptual boundaries, artifact expectations, and gates.

## 9. Why CGE is different

Traditional engineering governance often distributes responsibility across documents, reviews, checklists, and pipelines. That can be effective, but it is often fragmented.

CGE differs by integrating:

* context governance
* bounded execution
* canonical taxonomies and schemas
* authoritative structured artifacts
* mandatory audits
* validation gates
* reporting and drift-driven improvement

into a single operating model.

The result is not just better prompts. It is a more governable way to execute engineering work.

## 10. Reporting and actionable outputs

CGE treats reporting as a first-class capability. Reporting rolls up:

* task audits
* authoritative structured artifacts
* gate outcomes
* drift signals
* framework and context change history

These reports can produce vendor-neutral actionable outputs such as:

* **Features** for larger improvement themes
* **Stories** for concrete follow-up work items

This gives leadership visibility while preserving engineering traceability.

## 11. Organizational impact

CGE is designed to improve:

* execution consistency
* architecture adherence
* review efficiency
* reproducibility
* onboarding
* governance effectiveness
* evidence-based improvement

It provides a path for scaling AI assistance without allowing that assistance to weaken engineering discipline.

## 12. Conclusion

Context-Governed Engineering is an engineering operating model for governed SDLC execution. It transforms AI assistance from an informal interaction pattern into a bounded and evidence-based execution surface within a larger system of governance, validation, and continuous improvement.

---

# Document 2: Specification

## Context-Governed Engineering (CGE)

### Specification v1.0

## Authority

This document is normative and authoritative for CGE requirements, conformance, governance, canonical taxonomies, schemas, audits, reports, actionable outputs, and profiles.

**Normative language:** MUST, MUST NOT, SHOULD, MAY

## 0. Document layering and authority

1. The Executive Summary is informational.
2. The White Paper is explanatory.
3. This Specification is normative and authoritative for the CGE operating model.
4. Implementation Guides define environment-specific profiles and MUST NOT contradict this Specification.
5. Normative annexes incorporated by this Specification are authoritative to the extent stated by their annex designation.

## 1. Scope

1. CGE applies to governed engineering work across all SDLC phases.
2. CGE is platform-agnostic and tool-agnostic unless an approved profile states otherwise.
3. CGE applies to work products and lifecycle controls including analysis outputs, design decisions, implementations, tests, releases, and operational readiness artifacts.

## 2. Principles

1. **Governed context is authoritative.** Context defines applicable rules, vocabulary, patterns, constraints, taxonomies, and schemas.
2. **Work is bounded.** Engineering work is performed as atomic tasks bounded to exactly one conceptual boundary.
3. **Canonical evidence is required.** Authoritative outputs and audits provide durable evidence for validation and review.
4. **Reproducibility and attribution matter.** Outputs must be attributable to immutable identifiers for governing context and work state.
5. **Improvement is evidence-driven.** Drift signals and reporting inform refinement of the operating model.

## 3. Definitions

### 3.1 Context

The authoritative source of rules, vocabulary, patterns, constraints, taxonomies, and schemas that govern execution.

### 3.2 Task

An atomic unit of work that operates within exactly one conceptual boundary and produces governed outputs.

### 3.3 Workflow

An orchestration of tasks aligned to an SDLC phase with declared expected outputs, gates, and success criteria.

### 3.4 Conceptual boundary

A governed classification used to define and limit task scope within a phase-appropriate taxonomy.

### 3.5 Bounded

Bounded means that the task's scope, file modification surface where applicable, inputs, and expected outputs are limited to exactly one declared conceptual boundary.

### 3.6 Gate

A defined check that evaluates required outputs or conditions and produces a pass or fail outcome.

### 3.7 Artifact classes

* **Authoritative Structured Artifact (ASA):** a structured artifact treated as the source of truth for validation, reporting, traceability, and reproducibility
* **Narrative Artifact:** human-readable rationale, explanation, or commentary
* **Rendering:** a derived representation of an ASA for human readability

### 3.8 Primary output artifact

The principal deliverable produced by a task for its declared conceptual boundary. A task MAY produce supporting artifacts, but MUST produce at least one primary output artifact.

### 3.9 Audit

A structured record of what was done, under what governing context, with what inputs, what outputs, what validation occurred, and what risks or assumptions remain.

### 3.10 Report

A structured rollup of audits and artifacts over a defined scope and time window.

### 3.11 Drift signal

A structured indicator derived from audits and reports that highlights repeated risks, assumptions, low-confidence patterns, gate failures, policy conflicts, or other indicators of operating model refinement needs.

### 3.12 Enlightenment

Enlightenment is the informal CGE term for the point at which a governed prompt or context has become highly stable through repeated evidence-driven refinement and rarely requires further updates. This stability is reached through mechanisms such as vibe sessions, audit rollup analysis, drift analysis, and governed refinement over time. Formally, enlightenment corresponds to a stabilized maturity state, not a permanent or irreversible condition.

### 3.13 Profile

A binding of this Specification to a specific environment, platform, enforcement surface, or execution surface.

### 3.14 Exception

A registered and governed deviation from a rule defined by this Specification or by a valid profile.

## 4. Conformance model

### 4.1 Conformance claim

1. An adoption of CGE MUST publish a conformance claim.
2. A conformance claim MUST include:

   * applicable Specification version
   * claimed conformance level
   * claiming scope
   * effective date
   * enabled profiles, if any
   * referenced exceptions, if any
3. The claiming scope MUST identify the organizational or technical scope of the claim, such as organization, platform implementation, repository, or workflow system.
4. A system MUST NOT claim a conformance level unless all mandatory requirements of that level are met, unless covered by a registered exception.
5. Systems that do not meet L1 are non-conformant and MUST NOT publish a conformance claim.

### 4.2 Conformance levels

Higher levels include all requirements of lower levels.

#### L1: Governed Execution

An L1-conformant system MUST:

1. enforce task boundedness to exactly one conceptual boundary
2. produce at least one primary output artifact per task
3. ensure each task primary output artifact is an ASA unless the workflow explicitly permits a governed narrative artifact as the primary output
4. produce exactly one task audit per task
5. store ASAs in canonical JSON form
6. preserve immutability of published audits and ASAs

#### L2: Governed Validation

An L2-conformant system MUST satisfy L1 and also MUST:

1. require workflows to declare SDLC phase, expected outputs, and gates
2. evaluate gates for required ASAs and produce pass or fail outcomes
3. record gate outcomes in audits
4. produce a run-level rollup artifact for workflow execution

#### L3: Governed Improvement

An L3-conformant system MUST satisfy L2 and also MUST:

1. derive drift signals from audits and gate outcomes
2. produce periodic reports at a governance-defined cadence
3. operate both enforcement and human governance feedback loops
4. support production of vendor-neutral actionable outputs derived from reporting

### 4.3 Minimum required level

Organization governance MAY define a minimum required conformance level by scope. The minimum required level MUST be stated in governance policy.

## 5. Governance and decision rights

### 5.1 Tiered governance

CGE MUST define tiered governance with decision rights and change control for:

1. **Tier 1:** core rules, schemas, canonical taxonomies, audit requirements, report requirements, and conformance model
2. **Tier 2:** workflow library, task catalogue, prompt library, gate definitions, and approved profile catalog
3. **Tier 3:** local context extensions and local additions

### 5.2 Evidence requirements

1. Tier 1 changes MUST require evidence derived from audits and reporting outputs.
2. Governance MUST define review cadence for Tier 1 assets, exceptions, and drift trends.

### 5.3 Exception decision rights

Governance MUST define, at minimum:

1. who may request an exception
2. who may approve an exception
3. the allowed scope of an exception
4. whether exceptions expire
5. review cadence for active exceptions

## 6. Canonical taxonomy model

### 6.1 Taxonomy governance

1. Canonical taxonomies are governed assets.
2. Canonical taxonomy identifiers MUST be defined in this Specification or in normative annexes referenced by it.
3. Profiles MAY define specialized taxonomy terms only when those terms map explicitly to canonical taxonomy identifiers.
4. Audits, reports, and conformance artifacts MUST use canonical taxonomy identifiers.

### 6.2 SDLC phase taxonomy

Canonical `sdlc_phase` values are defined in Annex A.

### 6.3 Project type taxonomy

Canonical `project_type` values are defined in Annex C.

### 6.4 Boundary taxonomy model

1. Conceptual boundaries MUST be selected from the approved taxonomy for the declared SDLC phase.
2. A task MUST NOT span multiple conceptual boundaries.
3. Profiles MAY define specialized boundaries only when they map explicitly to a canonical boundary defined by this Specification or Annex B.
4. Profiles that govern code-modifying tasks MUST define how allowed and disallowed modification surfaces are declared and enforced.

### 6.5 Artifact class taxonomy

Canonical artifact classes are:

* `asa`
* `narrative_artifact`
* `rendering`

### 6.6 Audit taxonomy

Canonical audit type values include:

* `task_audit`
* `vibe_session_audit`

### 6.7 Report taxonomy

Canonical report type values include:

* `run_report`
* `repository_report`
* `system_report`
* `catalogue_report`
* `drift_report`

### 6.8 Actionable output taxonomy

Canonical actionable output type values are:

* `feature`
* `story`

## 7. Context system requirements

### 7.1 Context layers

CGE uses three context layers:

1. **Global context:** authoritative
2. **Project context:** supplemental
3. **Execution context:** assembled for a run

### 7.2 Global context

Global context MUST:

1. be versioned and governed
2. define canonical taxonomy governance and references to approved taxonomies
3. define task catalogue governance
4. define audit requirements, report requirements, and artifact schema governance
5. define exception policy

### 7.3 Project context

Project context MAY extend global context by adding constraints, clarifications, specialized mappings, and additional gates.

Project context MUST NOT:

1. weaken global constraints
2. contradict global rules
3. disable required audits, required reports, or required reporting obligations

### 7.4 Execution context

Execution context MUST be attributable to immutable identifiers for:

1. governing context version
2. work state version
3. workflow definition version, when applicable
4. runner or execution surface version, when applicable

Execution context references MUST be recorded in task audits and other run artifacts where applicable.

## 8. Task and workflow requirements

### 8.1 Tasks

A task MUST:

1. declare SDLC phase
2. declare exactly one conceptual boundary
3. produce at least one primary output artifact
4. produce exactly one task audit artifact

A task MUST NOT span multiple conceptual boundaries.

### 8.2 Primary output artifact behavior

1. A task primary output artifact MUST be an ASA unless a workflow explicitly permits a governed narrative artifact as the primary output.
2. If a workflow requires structured validation, the task primary output artifact MUST be an ASA.
3. Supporting outputs MAY include narrative artifacts and renderings.

### 8.3 Workflows

A workflow MUST:

1. declare SDLC phase
2. define expected outputs
3. define gates and success criteria
4. ensure tasks remain bounded

## 9. Artifact model

### 9.1 Canonical format for ASAs

1. All ASAs MUST be stored in canonical JSON format.
2. Validation gates and reporting MUST evaluate the canonical JSON representation.
3. Where a project maintains a narrative representation of an ASA, the canonical JSON ASA remains authoritative.

### 9.2 Renderings

1. Renderings MAY be produced in other formats, including Markdown and YAML.
2. A rendering MUST be mechanically derivable from its canonical JSON ASA without semantic loss unless governance explicitly permits an explanatory rendering.
3. If a rendering conflicts with its canonical JSON ASA, the canonical JSON ASA is authoritative.

### 9.3 Narrative artifacts

1. Narrative artifacts MAY be used for rationale, explanation, and human decision support.
2. When a narrative artifact describes or summarizes an ASA, it MUST reference the ASA.
3. A narrative artifact MAY be a primary output artifact only when explicitly permitted by workflow definition and governance policy.

## 10. Audit model

### 10.1 Task audits are mandatory

Every task MUST produce exactly one task audit as an ASA.

### 10.2 Audit content requirements

Task audits MUST capture, at minimum:

1. identity and timing of the execution
2. declared SDLC phase and conceptual boundary
3. references to governing context version and work state version
4. inputs and outputs, where references are acceptable
5. prompts or instructions used, where references are acceptable
6. assumptions, risks, and confidence
7. gate outcomes, if applicable
8. exceptions used, if any

### 10.3 Confidence behavior

If confidence is below governance thresholds for the phase, the task MUST:

1. list missing information clearly
2. emit risks clearly
3. recommend follow-up
4. avoid guessing on high-impact decisions

### 10.4 Vibe session audits

CGE recognizes vibe sessions as governed exploratory execution. Vibe sessions MUST produce vibe session audits as ASAs.

The following distinctions apply:

1. `audit.task_audit` remains mandatory for bounded tasks.
2. `audit.vibe_session_audit` is used for governed exploratory vibe sessions.
3. A vibe session audit does not replace required task audits where formal bounded execution occurs.

If a vibe session produces code changes, test assets, operational artifacts, or design decisions intended for durable governed outputs, those outputs MUST be reconciled through bounded tasks with standard task audits.

Vibe session audits SHOULD be included in drift analysis and reporting.

## 11. Gates and validation

1. Workflows MUST define gates for required ASAs.
2. Gates MUST produce pass or fail outcomes.
3. Gate outcomes MUST be recorded in audits.
4. If governing rules conflict, the task result MUST be treated as failed and the audit MUST record:

   * conflicting rule references
   * impact statement
   * recommended remediation

## 12. Reporting, drift signals, and feedback loops

### 12.1 Reporting

1. Reporting MUST be treated as a first-class capability.
2. Reporting MUST aggregate audits and ASAs to produce rollups.
3. Reports MUST be ASAs and therefore MUST be canonical JSON.
4. Human-readable renderings MAY be produced.
5. Minimum report envelope and required report schemas are defined in Annex F.

### 12.2 Drift signals

1. Drift signals MUST be derived from audits and reports.
2. Drift signals SHOULD include repeated assumptions, repeated risks, low-confidence clusters, gate failures, policy conflicts, and rework indicators.
3. Drift signals MUST inform continuous improvement and governance actions.

### 12.3 Feedback loops

CGE MUST operate both:

1. enforcement feedback loops for correctness and gate compliance
2. human governance feedback loops for interpretation, prioritization, and policy changes

## 13. Actionable outputs

1. CGE MAY produce vendor-neutral actionable outputs derived from reporting.
2. Actionable outputs MUST be ASAs.
3. Actionable outputs MUST be classified as either `feature` or `story`.
4. Actionable outputs MUST include sufficient source references to audits and artifacts to support traceability.
5. Minimum fields for actionable outputs MUST include:

   * `actionable_output_type`
   * `title`
   * `problem_statement`
   * `recommended_action`
   * `priority`
   * `evidence_refs`
   * `derived_from_report_id`
   * `status`

## 14. Exceptions

1. Exceptions MUST be registered centrally with an identifier and rationale.
2. Exceptions MUST record scope, approver, status, and expiration policy.
3. Exceptions MUST be referenced by audits when used.
4. Exceptions MUST be reviewed on a governance-defined cadence.
5. Exceptions MUST NOT disable the requirement to produce audits.

## 15. Versioning, retention, and reproducibility

1. The CGE system MUST use semantic versioning for this Specification.
2. Audits, ASAs, and reports MUST record the immutable identifiers required to attribute outputs to:

   * governing context version
   * work state version
3. Reproducibility requirements and tolerances MUST be defined by governance policy and MAY vary by SDLC phase.
4. Governance MUST define retention policy for audits, ASAs, reports, and related renderings.
5. Once published under a run identifier, an audit or ASA MUST NOT be modified in place. Corrections MUST be published as a new artifact or audit that references the superseded item.

## 16. Profiles

1. Profiles MAY define bindings to specific environments, platforms, execution surfaces, or enforcement surfaces.
2. Profiles MUST NOT weaken this Specification.
3. A conformance claim MUST list enabled profiles.
4. Approved profile definitions and applicability are governed by Annex G.

## 17. What is explicitly not in this Specification

This Specification intentionally does not define:

1. exact CLI commands
2. exact CI job definitions
3. tool integration code
4. ticketing system integration details
5. repository bootstrapping scripts
6. environment-specific file paths

These belong in Implementation Guides and approved profiles that reference this Specification.

## Annex incorporation

The following annexes are normative and incorporated by reference into this Specification:

* **Annex A:** SDLC phase taxonomy
* **Annex B:** Conceptual boundary taxonomy by SDLC phase
* **Annex C:** Project type taxonomy and applicability
* **Annex D:** Artifact schema governance and schema catalog
* **Annex E:** Audit schema specification
* **Annex F:** Report schema set and minimum report requirements
* **Annex G:** Approved profiles and their applicability

---

# Document 3: Implementation Guide

## Context-Governed Engineering (CGE)

### Implementation Guide v1.0 (GitLab, Shell Runners, and Claude Code Execution Surface)

## Authority

This document defines an environment-specific implementation profile for CGE. It MUST NOT contradict the CGE Specification. Where this guide adds environment-specific rules, they are treated as profile requirements for the approved set4d profile defined in Annex G.

## Purpose

This guide defines a production-grade implementation of CGE for the set4d GitLab environment. It describes repository layout, the docs repository structure, including contexts, prompts, workflows, schemas, and runners, the execution model, including tasks and workflows, runner conventions, artifacts and audits, reporting and drift analysis, and CI integration.

This guide is written to make the operating model executable, reviewable, and distributable. It therefore explains each major CGE concept with concrete examples and notes on the value that concept provides to the operating model.

## Current Implementation Status

**This CGE repository is production-ready for:**
- ✅ C++ legacy code decomposition (file/class/algorithm extraction)
- ✅ Spring Boot feature implementation (4-phase workflow: define-project-context, decompose-feature, plan-service-or-feature, execute-work-plan)
- ✅ Spring Boot API documentation extraction
- ✅ Schema validation and gate enforcement
- ✅ Cost tracking and run reporting
- ✅ Checkpoint system for resumability
- ✅ Vibe session mode for artifact review

**Currently implemented:**
- 22 workflow YAML definitions (analysis, design, implementation, testing phases)
- 27 JSON schemas (artifacts, audits, reports, C++ decomposition, Spring Boot, modernization, verification)
- 32 task prompt templates
- 23 workflow wrapper scripts
- Core runner infrastructure (cge-run.sh, cge-task.sh, cge-validate.sh, cge-report.sh, cge-checkpoint.sh, cge-resume.sh)
- Python YAML parser (yq optional)
- Global context governance (patterns, best practices, security, auditing)

**Experimental or partial:**
- Drift analysis (reporting infrastructure exists, pattern detection rules under development)
- Actionable output generation (schema approved, generation logic partial)
- Multi-source modernization workflows (workflows exist, under active refinement)
- CI integration (documented but not packaged as ready-to-use CI templates)

**Documentation completeness:**
- Full specification (Document 2)
- Complete implementation guide (this document)
- Working end-to-end examples (context-service, C++ decomposition)
- Schema registry with approved schemas

For production use, focus on the workflows marked as production-ready above. The experimental features are functional but may evolve based on usage feedback.

## 0. Profile declaration for this guide

This guide defines the implementation behavior of the following approved profile:

* `profile.set4d.gitlab_shell_claude@1.0.0`

This profile assumes:

* GitLab repositories as governed execution surfaces
* shell runners as the orchestration mechanism
* Claude Code as the primary AI execution surface for bounded task execution
* canonical JSON ASAs as authoritative evidence outputs

## 0.1 What this guide demonstrates

This guide demonstrates how CGE is operationalized through:

* governed global and project context
* bounded task execution
* canonical structured artifacts
* mandatory task audits
* validation gates
* run reporting
* repository and system reporting
* drift analysis
* actionable output generation
* profile-based enforcement

## 0.2 Why examples matter in CGE

In CGE, examples are not only explanatory. Good examples demonstrate how a governed system turns engineering activity into durable evidence. Each example in this guide is intended to show one or more of the following forms of value:

* **execution value**: work is easier to perform consistently
* **review value**: work is easier to review because scope and evidence are bounded
* **traceability value**: outputs can be attributed to context, inputs, and work state
* **governance value**: policy conformance is easier to evaluate
* **improvement value**: recurring patterns can be reported and acted upon

## 1. Target environment

### 1.1 GitLab group topology

Root group:

* `set4d/`

Subgroups:

* `set4d/app/`

  * Spring Boot applications
  * Commons libraries (n-tier)
  * Core multi-module Maven project
  * Parent POM repository
* `set4d/data/`

  * Java Lambda runtime repositories
* `set4d/docs/`

  * CGE contexts, prompts, workflows, schemas, reports, and runners

### 1.2 Project types in this environment

This implementation assumes the following project categories are in scope:

* Spring Boot application repositories
* Java commons library repositories
* Multi-module Maven repositories
* Java Lambda runtime repositories
* Parent POM repositories

The canonical taxonomy and applicability rules remain governed by Annex C.

### 1.3 End-to-end example used throughout this guide

Many examples in this guide use a Spring Boot repository called `example-service` and a feature request such as:

> Add a new authorized endpoint to expose ingestion error summary metrics and emit an operational event when error counts exceed threshold.

This example is useful because it crosses several concerns that CGE must still keep bounded:

* API exposure
* service behavior
* repository or metrics lookup
* messaging or event publication
* observability
* security
* testing
* reporting

CGE adds value here by preventing this feature from being treated as one large, poorly governed change. Instead, the work is decomposed into bounded tasks that produce evidence at each step.

## 2. Repository roles and responsibilities

### 2.1 Application and library repositories (`set4d/app/*`)

Responsibilities:

* implement Spring Boot services and shared Java libraries
* own project context for the repository
* store durable CGE artifacts and audits required for traceability
* run CGE workflows in CI for governed changes

### 2.2 Lambda runtime repositories (`set4d/data/*`)

Responsibilities:

* implement Java Lambda runtimes, including AWS ingestion or transformation capabilities
* own project context for the repository
* store durable CGE artifacts and audits required for traceability
* run CGE workflows in CI for governed changes

### 2.3 Core multi-module Maven repository

This repository contains a shared multi-module core.

Minimum required modules for CGE alignment:

* `spring-boot-commons/`

  * contains the Spring Boot parent used by Spring applications
  * encodes cross-cutting conventions and shared dependencies

Guidance:

* Treat `spring-boot-commons` as a governance surface.
* Changes to shared build foundations SHOULD require higher evidence and additional gates.

### 2.4 Parent POM repository

This repository provides a parent POM that all projects inherit from.

Guidance:

* Treat the parent POM as a governance surface.
* Enforce dependency and plugin management conventions through gates.

### 2.5 Docs repository (`set4d/docs/`)

This repository is the central distribution point for:

* global context
* abstract prompts
* abstract workflows
* schema catalog
* report definitions
* runner scripts

This repository is the authoritative location for global CGE assets in the set4d environment.

### 2.6 Why this repository separation adds value to CGE

This separation creates value because:

* global governance assets are maintained centrally
* project-local context remains close to implementation reality
* evidence is stored with the repository that produced it
* reporting can aggregate from standardized outputs across many repositories

Without this separation, teams often mix reusable governance rules with local implementation details, making drift harder to detect.

## 3. Docs repository structure

### 3.1 Standard folder layout

```text
set4d/docs/
  cge/                            # CGE docs repository root (this repository)
    README.md                     # This document (specification + implementation guide)
    CLAUDE.md                     # Instructions for Claude Code
    PREREQUISITES.md              # Installation requirements
    test-setup.sh                 # Prerequisites verification script

    global_context/               # Authoritative governance
      patterns/                   # Architecture patterns (layered_architecture.md, api_design.md, etc.)
      best_practices/             # Engineering best practices (error_handling.md, observability.md, etc.)
      security/                   # Security standards (authorization.md, secrets.md, etc.)
      auditing/                   # Audit requirements (audit_requirements.md, vibe_session_rules.md)
      governance/                 # Governance model (governance_model.md, vibe_session_governance.md)

    prompts/
      tasks/                      # 32 task prompt templates
        analysis.*.md             # Analysis phase prompts (define-project-context, cpp_inventory, etc.)
        design.*.md               # Design phase prompts (decompose-feature, plan-service-or-feature, etc.)
        implementation.*.md       # Implementation phase prompts (implement-controller, implement-service, etc.)
        testing.*.md              # Testing phase prompts (define-test-strategy, implement-test-unit, etc.)

    workflows/
      definitions/                # 22 YAML workflow definitions
        analysis/                 # C++ decomposition, file/class inventory, algorithm extraction
        design/                   # Feature decomposition, planning, test strategy
        implementation/           # Work plan execution
        testing/                  # Test extraction and verification
        README.md                 # Workflow catalog

    schemas/                      # 27 JSON schemas + registry
      artifact/                   # ASA schemas (project_context, work_plan, decomposition_report, etc.)
      audit/                      # Audit schemas (task_audit, vibe_session_audit)
      cpp/                        # C++ decomposition schemas (file_inventory, class_inventory, algorithm, build_system)
      springboot/                 # Spring Boot schemas (api_contract, dto_contract)
      modernization/              # Modernization schemas (data_strategy, algorithm_implementation, domain_model)
      verification/               # Test schemas (test_suite, io_file_format, tolerance_spec)
      task/                       # Task checkpoint schema
      reporting/                  # Report schemas (run_report, drift_report)
      registry.json               # Schema catalog with status and compatibility

    runners/
      bin/                        # Core runner scripts
        cge-run.sh                # Main manifest-driven workflow orchestrator
        cge-task.sh               # Single bounded task executor
        cge-validate.sh           # Schema validation gates
        cge-report.sh             # Run report and drift analysis generator
        cge-checkpoint.sh         # Progress tracking and resumability
        cge-generate-audit.sh     # Auto-generate audit from artifacts
        cge-resume.sh             # Resume failed tasks
        yaml-query.py             # Python YAML parser (yq alternative)
        validate-*.sh             # Validation scripts
      workflows/                  # 23 convenience wrapper scripts
        cpp-decomposition.sh      # C++ analysis workflows (unified CLI)
        define-project-context.sh # Phase 1: analysis
        decompose-feature.sh      # Phase 2: design
        plan-service-or-feature.sh # Phase 3: design
        execute-work-plan.sh      # Phase 4: implementation
        decompose-*.sh            # Various decomposition workflows
      manifests/                  # Ready-to-use workflow manifests (if any)
        examples/

    reporting/
      definitions/
      drift_rules/

    examples/                     # Example artifacts and workflows
      end_to_end/                 # End-to-end workflow examples
        artifacts/                # Example ASAs (project_context, work_plan, audits, reports)
      cpp_decomposition/          # C++ decomposition examples
      api_decomposition/          # Spring Boot API examples
      modernization/              # Modernization examples
      verification/               # Test verification examples
      vibe_session/               # Vibe session audit examples

    docs/                         # Additional documentation
      vibe_sessions.md            # Vibe session mode guide
```

Notes:

* `global_context/` is authoritative.
* `prompts/` and `workflows/` are abstract and reusable via placeholders.
* `schemas/` contains canonical JSON schema definitions and a registry.
* `reporting/` contains reusable definitions for rollups and drift analysis.
* `runners/` contains shell runners and optional YAML manifests.

### 3.2 Versioning rules

* `set4d/cge/global_context/VERSION` stores the semantic version of global context.
* Every change to global context MUST be traceable to Git history.
* The global context version MUST be referenced by executions and recorded in audits.

### 3.3 Example of value provided by a governed docs repository

If the organization changes its audit requirements or introduces a new report schema, those changes can be made once in `set4d/docs` and then consumed consistently across repositories. That provides governance value and rollout efficiency.

## 4. Context architecture in GitLab

### 4.1 Global context

Global context lives in:

* `set4d/cge/global_context/`

It includes:

* architectural patterns and best practices
* security rules
* auditing rules
* task and workflow governance rules
* environment-specific execution guidance

### 4.2 Project context per project

Each project repository has a `.cge/` directory for CGE inputs and outputs. The only required human-authored seed is the feature request:

```text
<repo_root>/
  .cge/
    feature-request.md    (human-authored — describes the feature or service to build)
    runs/                 (generated — never hand-edited)
  .cursorrules            (human-authored — Cursor IDE CGE context hint)
      analysis/
        define-project-context-YYYYMMDD-NNN/
          artifacts/project_context/project_context.json
          audits/define-project-context.audit.json
      design/
        decompose-feature-YYYYMMDD-NNN/
        plan-service-or-feature-YYYYMMDD-NNN/
      implementation/
        execute-work-plan-YYYYMMDD-NNN/
```

Rules:

* `feature-request.md` is the single human-authored input seed. It describes the feature or service to build.
* `runs/` is entirely generated by CGE runners. Do not hand-edit files under `runs/`.
* `project_context.json` is the authoritative structured understanding of the project. It is produced by the `define-project-context` workflow reading `feature-request.md`.
* All run artifacts are stored under `runs/{phase}/{task-label}-{YYYYMMDD}-{NNN}/` matching the SDLC phase that produced them.

### 4.3 Execution context

Execution context is assembled at runtime by runners.

Execution context MUST include references to:

* global context identifier, for example docs repo commit SHA
* work state identifier, for example target repo commit SHA
* workflow definition identifier
* runner identifier and version
* approved profile identifier and version

These references MUST be recorded in task audits.

### 4.4 Example: project context and why it matters

For `example-service`, the project context may declare:

* Spring Boot application
* Postgres persistence
* Camel routes for ingestion orchestration
* Kafka for event publication
* security requirement for all external endpoints
* observability requirement for all critical request paths

This adds value to CGE because later tasks do not need to rediscover these rules informally. The rules are already governed inputs. That improves execution consistency and reduces repeat assumptions.

## 5. Prompts, tasks, and workflows

### 5.1 Prompt abstraction and placeholders

Prompts are abstract and use placeholders so they can be reused across:

* multiple repositories
* multiple SDLC phases
* multiple domains

Standard placeholder conventions:

* Shell environment variables: `${VAR_NAME}`
* Template placeholders for prompt content: `{{PLACEHOLDER}}`

Required standard placeholders (execution context):

* `{{PROJECT_NAME}}`
* `{{PROJECT_PATH}}`
* `{{SDLC_PHASE}}`
* `{{WORKFLOW_ID}}`
* `{{TASK_ID}}`
* `{{RUN_ID}}`
* `{{GLOBAL_CONTEXT_REF}}`
* `{{WORK_STATE_REF}}`
* `{{PROFILE_ID}}`
* `{{RUNNER_ID}}`

Input ref placeholders (dynamically resolved from manifest):

Each task input in the manifest has a `type` and `ref`. The runner resolves `{{TYPE_UPPERCASE_REF}}` automatically. Examples:

* manifest input `type: requirements` → `{{REQUIREMENTS_REF}}`
* manifest input `type: project_context` → `{{PROJECT_CONTEXT_REF}}`
* manifest input `type: decomposition_report` → `{{DECOMPOSITION_REF}}`
* manifest input `type: feature_request` → `{{FEATURE_REQUEST_REF}}`
* manifest input `type: task_catalogue` → `{{TASK_CATALOGUE_REF}}`

Runners MUST resolve all placeholders before execution and MUST record resolved values in audits.

### 5.2 Task catalogue

Tasks are bounded prompts executed under governed context.

In docs repo, store task prompt templates in:

* `set4d/cge/prompts/tasks/`

Recommended task prompt naming:

* `<sdlc_phase>.<boundary>.<task_name>.md`

Examples:

* `analysis.domain_vocabulary.define-project-context.md`
* `design.feature_decomposition.decompose-feature.md`
* `design.feature_decomposition.plan-service-or-feature.md`
* `implementation.service.implement-service.md`
* `testing.test_unit.implement-test-unit.md`

### 5.3 Workflow definitions

Workflows define:

* which tasks are executed
* required inputs
* expected outputs
* required gates

In docs repo, store workflow definitions in:

* `set4d/cge/workflows/definitions/`

Workflow definitions MUST be abstract and use placeholders.

### 5.4 Example: why tasks and workflows add value

Without tasks and workflows, the example feature could be handled through one large instruction asking an assistant to “add the endpoint, metrics, events, tests, and docs.”

In CGE, the same work is broken into bounded tasks such as:

1. `decompose-feature`
2. `plan.service-or-feature`
3. `implement-api-contract`
4. `implement-controller`
5. `implement-service`
6. `implement-messaging-and-events`
7. `implement-observability`
8. `implement-test-unit`
9. `implement-test-integration`

This adds value because each task becomes smaller, easier to review, and easier to validate against its declared boundary.

## 6. Artifacts and audits

### 6.1 Canonical artifacts

* ASAs MUST be stored as canonical JSON.
* YAML and Markdown MAY be produced as renderings.
* Canonical JSON is authoritative when conflicts exist.

### 6.2 Standard output locations

Each repository MUST store CGE run outputs in a predictable location.

```text
<repo_root>/.cge/
  runs/
    <run_id>/
      manifest.json
      audits/
        <task_id>.audit.json
      artifacts/
        <artifact_type>/
          <artifact_name>.json
      renderings/
        <artifact_type>/
          <artifact_name>.md
          <artifact_name>.yaml
      reports/
        run_report.json
        drift_report.json
      actionable_outputs/
        feature-001.json
        story-001.json
```

Guidance:

* `manifest.json` is an ASA capturing run metadata and a file index.
* Audits are immutable per run.
* Renderings are optional.
* `reports/` and `actionable_outputs/` make reporting and improvement evidence durable.

### 6.3 Immutability and corrections

* Published audits and ASAs MUST be immutable.
* If a correction is required, publish a new audit or artifact that references the prior version and states the correction reason.

### 6.4 Example artifact flow and why it matters

For the example feature, one run may produce:

* `project_context.json`
* `decomposition_report.json`
* `work_plan.json`
* task audits for each implementation task
* `run_report.json`
* `drift_report.json`
* a `story` actionable output for a missing observability standard discovered during implementation

This adds value because execution evidence is no longer trapped in chat history or merge request comments. It becomes governed, queryable, and reportable.

## 7. Runner model

### 7.1 Runner strategy

This environment uses shell runners (`.sh`) to execute workflows.

Two execution styles are supported:

1. **Dedicated workflow runners**

   * many `.sh` scripts, each implementing one workflow execution
2. **Manifest-driven runner**

   * a small number of stable runner scripts that execute based on a YAML manifest

The manifest-driven approach is recommended.

### 7.2 Runner responsibilities

All runners MUST:

1. resolve placeholders and assemble execution context
2. load global context references and project context inputs
3. execute tasks in a bounded sequence
4. emit canonical artifacts and exactly one audit per task
5. validate ASAs against schemas and record gate outcomes
6. produce a run manifest and, when required by conformance, a run report
7. invoke drift analysis when required by profile or workflow
8. emit actionable outputs when configured reporting rules derive them

### 7.3 Core runner scripts

In `set4d/cge/runners/bin/`:

* `cge-run.sh`

  * entry point for running a workflow
* `cge-task.sh`

  * executes a single task under context
* `cge-validate.sh`

  * validates JSON artifacts against schemas
* `cge-report.sh`

  * produces run, repository, or system reports and derives drift signals

#### 7.3.1 Required invocation flags

Every `claude` invocation in `cge-task.sh` MUST include:

| Flag | Purpose |
|------|---------|
| `--no-session-persistence` | context isolation between tasks |
| `--output-format json` | structured output for cost/turns capture |
| `--model <model>` | explicit model per task boundary |
| `--max-turns <n>` | runaway prevention guard |
| `--allowedTools <list>` | write scope derived from work plan `allowed_paths` |
| `--append-system-prompt <txt>` | CGE boundary context injection |
| `timeout 600s` | API hang protection |

Default model by boundary: `haiku` for `define_project_context`, `decompose_feature`, `dto`, `configuration`; `sonnet` for all others.

Default max-turns by boundary: `40` for `test_unit`/`test_integration`; `30` for `service`/`controller`/`adapters_and_integrations`; `20` for all others.

### 7.4 Profile-required execution semantics

The approved `profile.set4d.gitlab_shell_claude@1.0.0` defines the following mandatory execution semantics:

1. implementation workflows MUST support plan-first execution for governed feature work
2. ordered execution workflows MUST execute tasks in declared order
3. task execution MUST occur in fresh stateless invocation mode per task — every task runs as an independent `claude` process with `--no-session-persistence`; no session state persists between tasks; each task sees only what is present on the filesystem and in its resolved prompt
4. code-modifying tasks MUST declare allowed and disallowed modification surfaces; write scope is enforced via `--allowedTools` derived from `allowed_paths`
5. partial re-runs MUST create new run identifiers and new evidence outputs

### 7.5 Manifest-driven runner design

A manifest defines a workflow execution in YAML. The manifest is an input convenience, not authoritative output.

Recommended manifest location:

* `.cge/manifests/`

Example manifest:

```yaml
run:
  workflow_id: analysis.define-project-context
  project_path: /path/to/repo
  project_name: example-service
  sdlc_phase: analysis
  global_context_ref: set4d/docs@<sha>
  work_state_ref: <repo>@<sha>
  profile_id: profile.set4d.gitlab_shell_claude@1.0.0
  outputs_root: .cge/runs
  reporting:
    run_report: true
    drift_analysis: true
    actionable_outputs: true

tasks:
  - task_id: define-project-context
    boundary: domain_vocabulary
    inputs:
      project_context_path: .cge/project_context
      global_context_path: set4d/cge/global_context
    outputs:
      project_context:
        type: project_context
        canonical_json: artifacts/project_context/project_context.json
        rendering_markdown: renderings/project_context/project_context.md

gates:
  - gate_id: schema_validate
    applies_to:
      - artifacts/project_context/project_context.json
```

### 7.6 Why the manifest adds value

The manifest creates value because it declares execution intent before work is performed. It makes inputs, outputs, gates, and reporting obligations explicit, which improves reproducibility and reviewability.

## 8. Example tasks

### 8.1 Task: `create-or-update-project-context`

Goal: create or update project context for a repository based on a project context profile.

Inputs:

* `project_path`
* global context reference
* project context profile

Outputs:

* `project_context` (ASA)
* `task_audit` (ASA)

Read and write behavior:

Runner reads:

* `.cge/project_context/references.json`
* `.cge/project_context/profile.json`
* relevant repo files when the profile indicates a component is present

Claude reads:

* `CLAUDE.md`
* project context narrative, if present
* the profile file and any referenced inputs

Writes:

* canonical `project_context.json` under `.cge/runs/<run_id>/artifacts/project_context/`
* optional renderings under `.cge/runs/<run_id>/renderings/project_context/`
* audit under `.cge/runs/<run_id>/audits/`

#### 8.1.1 Value to CGE

This task adds value because later tasks consume a stable structured understanding of the repository instead of relying on ad hoc inspection. That reduces repeated assumptions across runs.

#### 8.1.2 Example output summary

A resulting `project_context.json` may record:

* project type: `spring_boot_app`
* components: `camel`, `kafka`, `postgres`, `observability`
* applicable implementation boundaries: `controller`, `service`, `repository`, `messaging_and_events`, `observability`, `security_controls`
* constraints: all external endpoints require authorization, all emitted events require correlation identifiers

### 8.2 Task: `decompose-feature`

Goal: produce a decomposition artifact for a feature request using project context.

Inputs:

* `project_context` (ASA)
* feature request text
* global context reference

Outputs:

* `decomposition_report` (ASA)
* `task_audit` (ASA)

#### 8.2.1 Example value to CGE

This task turns a single feature request into bounded work candidates. That lowers scope ambiguity and becomes the basis for planning, validation, and downstream reporting.

#### 8.2.2 Example decomposition output

A decomposition may identify candidate work items such as:

* add authorized API contract for error summary endpoint
* implement controller boundary for endpoint exposure
* implement service boundary for aggregation logic
* implement messaging boundary for threshold event publication
* implement observability boundary for metrics and structured logging
* implement test integration boundary for endpoint and event validation

### 8.3 Layered implementation task family

CGE supports implementation by architectural layer using a task family. Each task is bounded to exactly one implementation boundary.

Layer tasks are invoked in two ways:

* **All layers workflow:** run the full ordered set for a new feature
* **By-task execution:** re-run only selected layer tasks

#### 8.3.1 Common inputs for layer implementation tasks

All layer implementation tasks SHOULD require:

* `work_plan.json` (ASA) when operating in plan-first mode
* `project_context.json` (ASA)
* `decomposition_report.json` (ASA) when implementing features
* codebase state, represented by work state id

All layer implementation tasks MUST:

* declare a single boundary
* declare allowed file scope and out-of-scope paths
* declare required gates

#### 8.3.2 Example layer tasks

* `implement-api-contract`
* `implement-dto`
* `implement-controller`
* `implement-service`
* `implement-repository`
* `implement-messaging-and-events`
* `implement-adapters-and-integrations`
* `implement-configuration`
* `implement-observability`
* `implement-error-handling`
* `implement-test-unit`
* `implement-test-integration`
* `implement-test-contract`

#### 8.3.3 Example: `implement-observability`

Goal:

* add metrics, structured logs, and traces required for the new error-summary endpoint and threshold event path

Allowed paths:

* `src/main/java/**/observability/**`
* `src/main/java/**/config/**` when required for instrumentation wiring

Disallowed paths:

* `src/main/java/**/controller/**`
* `src/main/java/**/service/**`
* `pom.xml`

Example value to CGE:

This task creates strong review value because it isolates observability concerns from service logic. It also creates improvement value because repeated observability gaps become visible in drift reports.

## 9. Layered implementation workflows and runners

### 9.1 Workflow: `plan.service-or-feature`

Purpose: review inputs and task catalogue to produce a bounded work plan for implementation.

Inputs:

* `project_context.json`
* `decomposition_report.json`, when implementing a feature
* `task_catalogue` reference

Outputs:

* `work_plan.json` (ASA)
* task audit

Notes:

* This workflow MUST NOT modify code.
* The runner validates that selected tasks exist in the catalogue.

#### 9.1.1 Example value to CGE

A plan-first workflow prevents immediate uncontrolled code generation. It forces the system to declare intended tasks and order before changing the codebase.

#### 9.1.2 Example work plan content

A `work_plan.json` may declare:

1. `implement-api-contract`
2. `implement-controller`
3. `implement-service`
4. `implement-messaging-and-events`
5. `implement-observability`
6. `implement-test-unit`
7. `implement-test-integration`

### 9.2 Workflow: `execute.work-plan`

Purpose: execute the approved work plan as a strictly ordered task sequence.

Inputs:

* `work_plan.json` (ASA)

Outputs:

* canonical ASAs produced by each task, when applicable
* task audits for each executed task
* run report, for L2 and above
* drift report, when enabled by profile or governance
* actionable outputs, when derived by reporting rules

Execution rules:

* the runner MUST execute tasks in the order listed in `work_plan.json`
* the runner MUST clear context between tasks by invoking fresh execution per task
* the runner MUST enforce file-scope constraints per task

#### 9.2.1 Example value to CGE

Ordered task execution adds value because each boundary is handled independently and evidence is produced incrementally. If one task fails a gate, the failure is localized.

### 9.3 Runner: execute all layers

```bash
bash ../cge/runners/bin/cge-run.sh --manifest .cge/manifests/execute_work_plan.yml
```

### 9.4 Runner: execute by-task

Runners MUST support partial execution via flags:

```bash
bash ../cge/runners/bin/cge-run.sh --manifest .cge/manifests/execute_work_plan.yml \
  --only-tasks implement-service,implement-repository

bash ../cge/runners/bin/cge-run.sh --manifest .cge/manifests/execute_work_plan.yml \
  --from-task implement-service
```

### 9.5 Example: partial re-run value

Suppose the `implement-observability` task fails because required structured log fields are missing. CGE allows re-running only that task and downstream tests as a new run with new evidence. That reduces waste and preserves provenance.

## 10. Validation gates

### 10.1 Purpose of gates

Gates evaluate whether required outputs and execution behavior satisfy defined standards.

### 10.2 Common gate examples

* schema validation gate
* boundary compliance gate
* required-field gate
* naming convention gate
* security policy gate
* observability standard gate
* test evidence gate

### 10.3 Example gate set for the running feature example

```yaml
gates:
  - gate_id: schema_validate
    applies_to:
      - artifacts/project_context/project_context.json
      - artifacts/decomposition_report/decomposition_report.json
      - artifacts/work_plan/work_plan.json

  - gate_id: boundary_compliance
    applies_to:
      - audits/implement-controller.audit.json
      - audits/implement-service.audit.json
      - audits/implement-observability.audit.json

  - gate_id: observability_required_fields
    applies_to:
      - artifacts/observability/endpoint_error_summary_observability.json

  - gate_id: security_required_authorization
    applies_to:
      - artifacts/api_contract/error_summary_endpoint_contract.json
```

### 10.4 Why gates add value to CGE

Gates make CGE enforceable. Context alone does not prevent drift. Gates turn rules into explicit pass or fail outcomes that can be audited and reported.

## 11. Reporting and drift analysis

### 11.1 Why reporting is first-class in CGE

CGE does not stop at execution evidence. It uses that evidence to improve the operating model. Reporting creates visibility across tasks, runs, repositories, and systems. Drift analysis identifies repeated patterns that suggest the governance model, task catalogue, prompts, or shared foundations should be improved.

### 11.2 Run reporting

Every L2 or higher workflow execution SHOULD produce a `run_report.json`.

Example `run_report.json` summary:

```json
{
  "schema_id": "report.run_report",
  "schema_version": "1.0.0",
  "report_id": "report-run-20260305-001",
  "report_type": "run_report",
  "run_id": "run-20260305-190000",
  "workflow_id": "execute.work-plan",
  "summary": {
    "tasks_executed": 7,
    "tasks_passed": 6,
    "tasks_failed": 1,
    "gates_passed": 11,
    "gates_failed": 1
  },
  "findings": [
    "Observability task failed required structured log field gate",
    "Security and boundary compliance gates passed"
  ]
}
```

#### 11.2.1 Value to CGE

Run reporting provides immediate review value and governance value. Reviewers can understand the outcome of a workflow without manually reading every artifact first.

### 11.3 Repository reporting

A repository report aggregates runs over a time window.

Example repository report findings:

* repeated failures in `implement-observability`
* repeated assumptions about correlation identifiers in Kafka events
* successful security gates across all new endpoints
* elevated re-run rate for controller tasks after changes to shared API contract rules

#### 11.3.1 Value to CGE

Repository reporting identifies whether a repository has localized governance friction or recurring execution gaps. This is improvement value at the team level.

### 11.4 Drift analysis

Drift analysis derives structured signals from audits, gates, and reports.

Example drift report:

```json
{
  "schema_id": "report.drift_report",
  "schema_version": "1.0.0",
  "report_id": "drift-20260305-repo-example-service",
  "report_type": "drift_report",
  "scope": {
    "repository": "set4d/app/example-service"
  },
  "signal_counts": {
    "repeated_assumptions": 3,
    "repeated_risks": 1,
    "low_confidence_clusters": 0,
    "gate_failures": 4,
    "rework_patterns": 2
  },
  "signal_clusters": [
    {
      "signal_type": "gate_failures",
      "topic": "observability_required_fields",
      "count": 3,
      "recommended_action": "Create shared observability helper and strengthen project context examples"
    }
  ]
}
```

#### 11.4.1 Value to CGE

Drift analysis is where CGE becomes self-improving. Instead of treating every failure as isolated, it detects repeated patterns and turns them into change signals for governance and shared foundations.

### 11.5 Actionable outputs from reporting

A drift report may derive one or more actionable outputs.

Example derived story:

```json
{
  "schema_id": "artifact.actionable_output",
  "schema_version": "1.0.0",
  "actionable_output_type": "story",
  "title": "Standardize observability fields for threshold event workflows",
  "problem_statement": "Three recent runs failed the observability_required_fields gate in example-service.",
  "recommended_action": "Create shared logging and metrics conventions for threshold event workflows and update implement-observability task guidance.",
  "priority": "high",
  "derived_from_report_id": "drift-20260305-repo-example-service",
  "evidence_refs": [
    "report:drift-20260305-repo-example-service"
  ],
  "status": "proposed"
}
```

#### 11.5.1 Value to CGE

This creates leadership value and improvement value. Execution evidence is translated into concrete follow-up work instead of being lost in retrospective discussion.

## 11.6 Vibe session mode for artifact review

CGE includes **vibe session mode** for governed exploratory review of completed workflow artifacts. Vibe sessions enable interactive artifact evaluation while maintaining full traceability and audit compliance.

### 11.6.1 What is a vibe session?

A vibe session is a governed exploratory session for:
- Reviewing completed workflow artifacts in `.cge/runs/` folders
- Evaluating artifact quality, completeness, and schema compliance
- Enriching documentation or fixing issues
- Identifying gaps and improvement opportunities
- Planning follow-up bounded tasks

Vibe sessions are automatically detected when working with `.cge/` artifacts and produce structured audit records (`audit.vibe_session_audit@1.0.0`).

### 11.6.2 Session modes

- **review**: Post-run artifact evaluation and improvement (primary use case)
- **exploration**: Pre-workflow exploratory analysis
- **decomposition**: Derive candidate bounded tasks
- **planning**: Shape work before execution
- **drafting**: Create rough candidate artifacts
- **remediation**: Investigate failures and propose fixes

### 11.6.3 Governance model

Vibe sessions follow CGE governance principles:
- **Bounded scope**: Limited to `.cge/runs/` artifacts (not CGE infrastructure)
- **Audit trail**: Track all modifications with provenance
- **Boundary enforcement**: Separate audits per conceptual boundary
- **Non-authoritative outputs**: Require reconciliation through bounded tasks for production use

Audits are only created when artifacts are modified (read-only sessions produce no audit).

### 11.6.4 Example vibe session audit

```json
{
  "schema_id": "audit.vibe_session_audit",
  "session_id": "vibe-20260308-143022",
  "run_id": "20260308-001/analysis/cpp-decomposition",
  "session_mode": "review",
  "objective": "Evaluate class inventory completeness",
  "scope": {
    "exploration_status": "crossed_into_governed_execution",
    "primary_boundary": "cpp_class_inventory",
    "files_modified": [
      ".cge/runs/20260308-001/artifacts/cpp_class_inventory/class-inventory.json"
    ]
  },
  "summary": "Reviewed class inventory, identified 3 missing relationships, added cross-references",
  "confidence": "medium",
  "follow_up": {
    "candidate_tasks": [
      {
        "title": "Extract template specialization relationships",
        "conceptual_boundary": "cpp_class_inventory",
        "reason": "Current inventory incomplete for templated classes"
      }
    ],
    "requires_bounded_task_conversion": true
  }
}
```

### 11.6.5 Integration with reporting

Vibe sessions are included in run reports and drift analysis:
- Run reports show vibe session counts and findings
- Drift analysis detects patterns like repeated low-confidence sessions or boundary crossings
- Candidate tasks from vibe sessions can be converted to bounded work

### 11.6.6 Documentation

See `docs/vibe_sessions.md` for:
- Complete usage guide
- Session lifecycle details
- Audit schema requirements
- Validation and verification
- Best practices and examples

## 12. CI integration (GitLab)

### 12.1 Pipeline objectives

GitLab CI SHOULD:

* run CGE workflows for governed work
* validate canonical artifacts against schemas
* enforce gates as merge request requirements
* publish audits and artifacts as job artifacts
* publish reports and actionable outputs as CI artifacts when generated

### 12.2 Recommended CI stages

* `validate-context`
* `run-workflow`
* `validate-artifacts`
* `publish-evidence`
* `publish-reports`

### 12.3 Merge request gating

For repositories in scope:

* gate failures MUST fail the pipeline
* required gates MUST be enforced as merge request approval conditions
* evidence outputs MUST be retained per governance policy

### 12.4 Example minimal `.gitlab-ci.yml`

```yaml
stages:
  - cge

cge:run:
  stage: cge
  image: alpine:3.20
  before_script:
    - apk add --no-cache bash git yq jq
    - git clone "${CGE_DOCS_REPO_URL}" cge-docs
    - chmod +x cge-docs/runners/bin/*.sh
  script:
    - bash cge-docs/runners/bin/cge-run.sh --manifest .cge/manifests/feature_impl.yml
    - bash cge-docs/runners/bin/cge-report.sh repository-rollup --repo-path . --window-days 30
  artifacts:
    when: always
    paths:
      - .cge/runs/
  rules:
    - if: "$CI_PIPELINE_SOURCE == 'merge_request_event'"
```

### 12.5 Value to CGE

CI integration turns local execution discipline into organization-scale enforcement and evidence retention.

## 13. Security and secrets

1. Runners MUST NOT log secrets.
2. Secrets required for execution MUST be provided via approved secret management.
3. Context documents SHOULD avoid embedding secrets and SHOULD reference secret identifiers instead.
4. Audits MUST NOT capture secret values.

### 13.1 Example value to CGE

This protects the evidence system itself. Audits and artifacts are intended for retention and review, so they must remain safe to store and inspect.

## 14. Operational governance

### 14.1 Change control

* global context changes are governed
* prompt and workflow libraries are governed
* local project contexts are governed within the project repository
* reporting rules and drift thresholds are governed assets

### 14.2 Evidence requirements

* changes to global context SHOULD require evidence derived from audits and reports
* changes to shared build foundations SHOULD require additional gates
* changes to drift rules SHOULD reference observed report data when possible

### 14.3 Example governance loop

1. multiple repositories show repeated observability gate failures
2. repository and system drift reports cluster the failures
3. governance reviews the evidence
4. governance updates the observability pattern library and task prompt guidance
5. subsequent runs show lower failure rates

### 14.4 Value to CGE

This is the closed-loop improvement model. Governance changes are driven by evidence rather than intuition alone.

## 15. Adoption sequence for set4d

1. Stand up `set4d/docs` CGE structure, including contexts, prompts, workflows, schemas, reporting definitions, and runners.
2. Add `.cge/project_context/` to 1 to 2 pilot repositories and implement `create-or-update-project-context`.
3. Add manifest-driven `cge-run.sh` execution for `decompose-feature` in pilot repositories.
4. Add schema validation gates and publish evidence in GitLab CI.
5. Add run reporting and basic drift analysis to pilot repositories.
6. Expand the task catalogue and workflow library incrementally.
7. Standardize enforcement and reporting cadences.
8. Derive actionable outputs from drift reports and feed them into governance and delivery planning.

## 16. Partial re-runs and task redo

CGE supports re-running a workflow to redo one or more tasks without re-running the entire workflow.

### 16.1 Core rules

1. A partial re-run MUST be treated as a new run.
2. A partial re-run MUST have a new `run_id`.
3. A partial re-run MUST produce new audits and new canonical ASAs under the new run directory.
4. A partial re-run MAY reference prior run outputs as inputs, but those references MUST be explicit and recorded.

### 16.2 Runner flags

Runners SHOULD support at least these flags:

* `--only-tasks <task_id,task_id,...>`
* `--from-task <task_id>`
* `--set <path=value>`

### 16.3 Provenance recording

When a partial re-run occurs, the run manifest and task audits MUST record:

* `rerun_of_run_id`
* `rerun_mode`
* `rerun_task_subset`
* `input_overrides`

### 16.4 Task behavior expectations

Tasks SHOULD be designed to be re-runnable by:

* reading canonical artifacts as inputs
* reconciling outputs to a declared desired state
* avoiding hidden state outside declared inputs

If a task cannot be safely re-run, the task catalogue entry MUST declare:

* why it is not re-runnable
* what preconditions are required
* what human verification is required

### 16.5 Example value to CGE

Partial re-runs create efficiency without sacrificing traceability. Teams can fix one failed boundary and preserve the historical record of both the original and corrected runs.

## 17. Building and running CGE from scratch

This section provides a complete, step-by-step implementation guide. It is written to be executable: following these steps recreates the full CGE docs repository and runs the context-service end-to-end example.

### 17.1 Prerequisites

Verify:
```bash
yq --version    # >= 4.x
jq --version    # >= 1.6
claude --version
```

### 17.2 CGE docs repository structure

Create the following directory tree. Every file listed is required for the runners to work.

```text
cge/                          (the CGE docs repository root)
  cge.md                      (this document)
  README.md                   (quick-start guide)
  global_context/
    VERSION                   (contains: 1.0.0)
    patterns/
    security/
    auditing/
    governance/
  prompts/
    tasks/
      analysis.domain_vocabulary.define-project-context.md
      design.feature_decomposition.decompose-feature.md
      design.feature_decomposition.plan-service-or-feature.md
      implementation.configuration.implement-configuration.md
      implementation.api_contract.implement-api-contract.md
      implementation.dto.implement-dto.md
      implementation.controller.implement-controller.md
      implementation.service.implement-service.md
      implementation.adapters_and_integrations.implement-adapters-and-integrations.md
      implementation.observability.implement-observability.md
      implementation.error_handling.implement-error-handling.md
      implementation.security_controls.implement-security-controls.md
      implementation.repository.implement-repository.md
      implementation.messaging_and_events.implement-messaging-and-events.md
      testing.test_strategy.define-test-strategy.md
      testing.test_unit.implement-test-unit.md
      testing.test_integration.implement-test-integration.md
      testing.test_contract.implement-test-contract.md
  workflows/
    definitions/
      analysis.define-project-context.yml
      design.feature-intake-and-decomposition.yml
      design.plan-service-or-feature.yml
      implementation.execute-work-plan.yml
  schemas/
    artifact/
      project_context.schema.json
      decomposition_report.schema.json
      work_plan.schema.json
      run_manifest.schema.json
      actionable_output.schema.json
    audit/
      task_audit.schema.json
    task/
      checkpoint.schema.json
    reporting/
      run_report.schema.json
      drift_report.schema.json
    registry.json
  runners/
    bin/
      cge-run.sh              (main manifest-driven workflow runner)
      cge-task.sh             (single bounded task executor)
      cge-validate.sh         (gate validation runner)
      cge-report.sh           (report generator)
      cge-checkpoint.sh       (checkpoint management utility)
      cge-generate-audit.sh   (auto-generate audit from artifacts)
      cge-resume.sh           (resume failed tasks)
    workflows/
      define-project-context.sh      (Phase 1 convenience wrapper)
      decompose-feature.sh           (Phase 2 convenience wrapper)
      plan-service-or-feature.sh     (Phase 3 convenience wrapper)
      execute-work-plan.sh           (Phase 4 convenience wrapper)
    manifests/
      examples/
        define-project-context.example.yml
        feature-impl.example.yml
  reporting/
    definitions/
    drift_rules/
  examples/
    end_to_end/
      README.md
      artifacts/
        project_context.example.json
        decomposition_report.example.json
        work_plan.example.json
        task_audit.example.json
        run_report.example.json
        drift_report.example.json
        actionable_output.example.json
```

### 17.3 Runner system internals

#### 17.3.1 cge-run.sh — manifest runner

`cge-run.sh` is the main entry point. It:

1. Reads a YAML manifest (`.yml`) passed via `--manifest`
2. Derives the run ID: `{YYYYMMDD-NNN}/{sdlc_phase}/{workflow-label}`
   - Finds the next sequence number (NNN) for the current date across all phases
   - For all manifests: label = workflow_id suffix
3. Creates run directory structure under `.cge/runs/{run_id}/`
4. Exports execution context as environment variables
5. For each task in the manifest, invokes `cge-task.sh`
6. For each gate, invokes `cge-validate.sh`
7. Writes a `manifest.json` run record
8. Generates run reports via `cge-report.sh`

Run directory layout produced:
```text
.cge/runs/{YYYYMMDD-NNN}/{phase}/{workflow-label}/
  manifest.json           (run record)
  prompts/                (resolved prompt files, one per task)
  checkpoints/             (task checkpoints for resumability)
  audits/                 (task audit JSON files)
  artifacts/              (canonical ASA JSON files)
  renderings/             (optional markdown renderings)
  reports/                (run report with aggregated costs, drift report)
  gates/                  (gate outcome JSON files)
  actionable_outputs/     (optional actionable output files)
```

#### 17.3.2 cge-task.sh — task executor

`cge-task.sh` handles one task. It:

1. Reads task fields from the manifest (task_id, boundary, prompt_ref, inputs)
2. Loads the prompt template from `{CGE_DOCS_ROOT}/{prompt_ref}`
3. Resolves ALL placeholders via string replacement:
   - Standard: `{{PROJECT_NAME}}`, `{{RUN_ID}}`, `{{PROJECT_PATH}}`, etc.
   - Dynamic inputs: for each manifest input, `{{TYPE_UPPERCASE_REF}}` → the input `ref` value
     - Example: `type: requirements, ref: .cge/feature-request.md` → `{{REQUIREMENTS_REF}}`
     - Example: `type: project_context, ref: /path/to/project_context.json` → `{{PROJECT_CONTEXT_REF}}`
4. Writes the resolved prompt to `{RUN_DIR}/prompts/{task_id}.resolved.md`
5. Invokes `claude` with the full required flag set and captures JSON output:
   ```bash
   CLAUDE_OUTPUT=$(timeout 600s claude \
     --no-session-persistence \
     --output-format json \
     --model "${TASK_MODEL}" \
     --max-turns "${TASK_MAX_TURNS}" \
     --allowedTools "${ALLOWED_TOOL_ARGS}" \
     --append-system-prompt "${BOUNDARY_SYSTEM_PROMPT}" \
     --dangerously-skip-permissions \
     -p "${CLAUDE_PROMPT}")
   ```
6. Extracts cost and usage metrics from the JSON output:
   - `cost_usd` - Total API cost in USD
   - `num_turns` - Number of agentic turns (API round-trips)
   - `input_tokens` - Total input tokens consumed
   - `output_tokens` - Total output tokens generated
   - `total_tokens` - Sum of input and output tokens
7. Writes metrics to `{RUN_DIR}/logs/{task_id}.metrics.json`:
   ```json
   {
     "task_id": "implement-controller",
     "model": "claude-3-5-sonnet-20241022",
     "cost_usd": 0.42,
     "num_turns": 8,
     "input_tokens": 12450,
     "output_tokens": 3220,
     "total_tokens": 15670,
     "timestamp": "2026-03-06T17:35:00Z"
   }
   ```
8. Verifies the audit file exists after execution

#### 17.3.3 cge-report.sh — report generator
      cge-checkpoint.sh       (checkpoint management utility)
      cge-generate-audit.sh   (auto-generate audit from artifacts)
      cge-resume.sh           (resume failed tasks)

`cge-report.sh` generates run reports and drift reports. For run reports, it:

1. Aggregates task audits and gate outcomes
2. Counts tasks passed/failed and gates passed/failed
3. Collects findings from audits (low confidence, excessive assumptions)
4. **Aggregates cost and usage metrics** from all `logs/*.metrics.json` files:
   - Sums `cost_usd` across all tasks
   - Sums `num_turns` across all tasks
   - Sums `input_tokens` and `output_tokens` across all tasks
5. Writes aggregated cost data to the `metadata` section of `run_report.json`:
   ```json
   {
     "schema_id": "report.run_report@1.0.0",
     "report_id": "...",
     "run_id": "20260306-001/implementation/execute-work-plan",
     "workflow_id": "implementation.execute-work-plan",
     "summary": {
       "tasks_executed": 7,
       "tasks_passed": 7,
       "tasks_failed": 0,
       "gates_passed": 1,
       "gates_failed": 0
     },
     "findings": [...],
     "metadata": {
       "total_cost_usd": 3.47,
       "total_turns": 52,
       "total_input_tokens": 87340,
       "total_output_tokens": 22580,
       "total_tokens": 109920
     },
     "generated_at": "2026-03-06T17:44:44Z"
   }
   ```
6. Prints cost summary to console: `Total cost: $3.47 USD (52 turns, 109920 tokens)`

This cost tracking enables:
- **Budget accountability** - Track actual AI API costs per workflow
- **Efficiency analysis** - Compare costs across similar tasks or workflow runs
- **Capacity planning** - Estimate costs for future work based on historical data
- **Boundary optimization** - Identify high-cost tasks that may need model or scope adjustments


#### 17.3.4 Checkpoint and resumability system

CGE includes a checkpoint system for long-running tasks that enables progress tracking and recovery from failures.

**Key components:**

1. **cge-checkpoint.sh** - Checkpoint management utility
   - Creates checkpoint files tracking task progress, artifacts, and metrics
   - Updates checkpoint state during task execution
   - Scans artifacts directory to inventory produced files
   - Checkpoint file: `.cge/runs/{run_id}/checkpoints/{task_id}.checkpoint.json`

2. **cge-generate-audit.sh** - Auto-generate audit from artifacts
   - Generates valid audit files from existing artifacts when task fails without producing audit
   - Reads manifest and scans artifact directories
   - Creates audit conforming to `audit.task_audit@1.0.0` schema

3. **cge-resume.sh** - Resume failed tasks
   - Resumes tasks that failed but produced artifacts
   - Auto-generates audit for `failed_missing_audit` status
   - Updates checkpoint to `completed` after recovery
   - Example:
     ```bash
     bash runners/bin/cge-resume.sh .cge/runs/20260306-001/analysis/decompose-feature extract-algorithms
     ```

**Checkpoint workflow:**

1. Task starts → checkpoint created with `in_progress` status
2. Task executes → checkpoint updated with metrics (cost, turns, tokens)
3. Task completes → artifacts scanned and checkpoint updated
4. If audit missing → checkpoint set to `failed_missing_audit` with resume instructions
5. If audit present → checkpoint set to `completed`

**Resume workflow:**

1. Task fails but produces artifacts (e.g., missing audit file)
2. Checkpoint shows `failed_missing_audit` status and `can_resume: true`
3. User runs `cge-resume.sh` with run directory and task ID
4. System auto-generates audit from existing artifacts
5. Checkpoint updated to `completed` status
6. Task marked as successfully completed

This enables:
- **Cost savings** - Don't lose progress from expensive AI runs
- **Time savings** - Resume from checkpoint instead of full retry
- **Debugging** - See exactly where task failed and what was produced
- **Monitoring** - Track progress of long-running tasks

#### 17.3.5 Convenience wrappers

Each workflow has a shell wrapper in `runners/workflows/` that builds an inline manifest and calls `cge-run.sh`. The four wrappers are:

| Wrapper | SDLC Phase | Creates |
|---------|-----------|---------|
| `define-project-context.sh` | analysis | `project_context.json` |
| `decompose-feature.sh` | design | `decomposition_report.json` |
| `plan-service-or-feature.sh` | design | `work_plan.json` |
| `execute-work-plan.sh` | implementation | all project source files |

### 17.5 SDLC phase alignment

CGE uses these four phases for the context-service workflow:

| Phase | Folder prefix | Tasks |
|-------|--------------|-------|
| `analysis` | `analysis/` | define-project-context |
| `design` | `design/` | decompose-feature, plan-service-or-feature |
| `implementation` | `implementation/` | execute-work-plan (7 sub-tasks) |
| `testing` | `testing/` | implement-test-unit, implement-test-integration |

Note: testing tasks are declared as `sdlc_phase: testing` inside work_plan.json tasks, but the execute-work-plan run itself is `sdlc_phase: implementation` because it drives the implementation workflow.

### 17.6 Task prompt file naming

Each task prompt file is named: `{sdlc_phase}.{boundary}.{task-name}.md`

The `sdlc_phase` prefix is the phase *of the task*, not the workflow:

| Task | File |
|------|------|
| define-project-context | `analysis.domain_vocabulary.define-project-context.md` |
| decompose-feature | `design.feature_decomposition.decompose-feature.md` |
| plan-service-or-feature | `design.feature_decomposition.plan-service-or-feature.md` |
| implement-configuration | `implementation.configuration.implement-configuration.md` |
| implement-service | `implementation.service.implement-service.md` |
| implement-test-unit | `testing.test_unit.implement-test-unit.md` |

### 17.7 Prompt template structure

Every task prompt template MUST contain:

```markdown
## Task metadata
- **task_id**: `{task-id}`
- **sdlc_phase**: `{phase}`
- **conceptual_boundary**: `{boundary}`
- **schema_ref**: `artifact.{artifact_type}@1.0.0`
- **profile**: `{{PROFILE_ID}}`

## Execution context
- **Run ID**: `{{RUN_ID}}`
- **Project path**: `{{PROJECT_PATH}}`
- **Global context ref**: `{{GLOBAL_CONTEXT_REF}}`
- **Work state ref**: `{{WORK_STATE_REF}}`

## Instructions
### Step 1: Read inputs
- `{{INPUT_TYPE_REF}}` — description

### Step N: Produce the output ASA
Write to: `{{PROJECT_PATH}}/.cge/runs/{{RUN_ID}}/artifacts/{type}/{name}.json`

### Step N+1: Produce the task audit
Write to: `{{PROJECT_PATH}}/.cge/runs/{{RUN_ID}}/audits/{task-id}.audit.json`

## Allowed paths (write)
- `.cge/runs/{{RUN_ID}}/artifacts/{type}/`
- `.cge/runs/{{RUN_ID}}/audits/`
```

### 17.8 Workflow manifest format

A YAML workflow manifest has three sections:

```yaml
run:
  workflow_id: analysis.define-project-context
  project_path: /path/to/project
  project_name: my-service
  sdlc_phase: analysis
  global_context_ref: local@HEAD
  work_state_ref: my-service@HEAD
  profile_id: profile.set4d.gitlab_shell_claude@1.0.0
  outputs_root: .cge/runs
  reporting:
    run_report: true
    drift_analysis: false
    actionable_outputs: false

tasks:
  - task_id: define-project-context
    boundary: domain_vocabulary
    prompt_ref: prompts/tasks/analysis.domain_vocabulary.define-project-context.md
    inputs:
      - type: requirements
        ref: .cge/feature-request.md

gates:
  - gate_id: schema_validate
    applies_to:
      - artifacts/project_context/project_context.json
    schema_id: artifact.project_context
    schema_version: 1.0.0
    required: true
```

Key rules:
- `prompt_ref` is relative to `CGE_DOCS_ROOT` (the `cge/` directory)
- `inputs[].ref` is an absolute or project-relative path; resolved into prompt via `{{TYPE_UPPERCASE_REF}}`
- `allowed_paths` in execute-work-plan tasks MUST use `**` wildcards for audit paths (e.g., `.cge/runs/**/audits/**`), never `{{RUN_ID}}` literals

### 17.8 End-to-end: context-service

#### Setup — context-service project seed

The context-service directory contains three human-authored files before running the workflow:

```text
context-service/
  .cge/
    feature-request.md      (describes the feature or service to build)
  .cursorrules              (Cursor IDE hint — references CGE global context)
  run-workflow.sh           (orchestrator — chains all 4 phases)
```

`feature-request.md` content:
```markdown
Create a new Spring Boot Context Service from scratch.

The service must:
- Expose GET /api/v1/contexts/{id} — returns a Context enriched with displayName from PreferencesService
- Expose POST /api/v1/contexts — creates a new context (in-memory store)
- All endpoints require ROLE_USER authorization
- Use WebClient to call an external PreferencesService at GET /preferences/{contextId}
- Include Micrometer counter: context.fetch.count (tag: status=found|not_found)
- Maven project, Spring Boot 3.3.x
- Unit tests for ContextService (mock WebClient) and ContextController (@WebMvcTest)
- No integration tests
```

`.cursorrules` content — tells Cursor IDE to reference CGE global context when generating AI-assisted files. Note: any CLAUDE.md generated from this `.cursorrules` (e.g. via `/init`) will include two explicit compliance instructions verbatim at the top of the CGE Global Context section: (1) read all files under `../cge/global_context/` before making changes, and (2) verify changes conform to CGE rules before completing any task.
```markdown
# CGE-Governed Project

This project is governed by CGE (Context-Governed Engineering) global context v1.0.0.
The authoritative global context lives at `../cge/global_context/`.

When generating a CLAUDE.md (e.g. via `/init`), include a section referencing the CGE global context
and summarize the key governing rules from these files:

- `patterns/layered_architecture.md` — Strict layer separation (Controller → Service → Repository/Adapters). Controllers must not contain business logic. Services must not contain persistence code.
- `patterns/api_design.md` — Kebab-case URLs, plural nouns, versions in path (`/api/v1/...`), standard HTTP status codes, JSON bodies, correlation ID propagation, all external endpoints require auth.
- `patterns/event_messaging.md` — Standard event envelope (eventId, eventType, correlationId, timestamp, source, payload). Kafka topic naming: `<domain>.<entity>.<action>`. Idempotent consumers with dead-letter handling.
- `best_practices/error_handling.md` — Standard error envelope: `{ "error": { "code", "message", "correlationId", "timestamp", "details" } }`. Domain exceptions mapped at controller boundary. External calls use circuit breakers or retry with backoff.
- `best_practices/observability.md` — Structured logs with mandatory fields (timestamp, level, service, correlationId, message, context). Required metrics: `http_requests_total`, `http_request_duration_seconds`, `errors_total`. W3C TraceContext/B3 propagation.
- `security/authorization.md` — Auth enforced at framework level (Spring Security), not business logic. RBAC default. Auth failures return 401/403, never 404/500.
- `security/secrets.md` — No hardcoded secrets. Config uses placeholders (`${DB_PASSWORD}`). Secrets from vault, CI/CD, or managed stores only.
- `auditing/audit_requirements.md` — Every task execution produces one task audit with provenance, confidence rating, gate outcomes, and assumptions.
- `governance/governance_model.md` — Three tiers: Tier 1 (global), Tier 2 (workflows), Tier 3 (local). Local context must not contradict global rules. Exceptions require scope, rationale, approver, and expiry.
```

`run-workflow.sh` content — chains all four phases:
```bash
#!/usr/bin/env bash
set -euo pipefail
CGE_ROOT="$(cd "$(dirname "$0")/../cge" && pwd)"
PROJECT_PATH="$(cd "$(dirname "$0")" && pwd)"
FEATURE_REQUEST="${PROJECT_PATH}/.cge/feature-request.md"

# Phase 1: define-project-context (analysis)
bash "${CGE_ROOT}/runners/workflows/define-project-context.sh" \
  --project-path "${PROJECT_PATH}" \
  --project-name context-service \
  --requirements-ref "${FEATURE_REQUEST}"

CTX=$(find "${PROJECT_PATH}/.cge/runs/analysis" -name "project_context.json" | sort | tail -1)

# Phase 2: decompose-feature (design)
bash "${CGE_ROOT}/runners/workflows/decompose-feature.sh" \
  --project-path "${PROJECT_PATH}" \
  --feature-request "${FEATURE_REQUEST}" \
  --project-context-ref "${CTX}"

DECOMP=$(find "${PROJECT_PATH}/.cge/runs/design" -name "decomposition_report.json" | sort | tail -1)

# Phase 3: plan (design)
bash "${CGE_ROOT}/runners/workflows/plan-service-or-feature.sh" \
  --project-path "${PROJECT_PATH}" \
  --project-context-ref "${CTX}" \
  --decomposition-ref "${DECOMP}"

PLAN=$(find "${PROJECT_PATH}/.cge/runs/design" -name "work_plan.json" | sort | tail -1)

# Phase 4: execute (implementation)
bash "${CGE_ROOT}/runners/workflows/execute-work-plan.sh" \
  --project-path "${PROJECT_PATH}" \
  --work-plan-ref "${PLAN}"
```

#### Directory assumptions

The context-service directory must be a sibling of the `cge/` directory:
```text
Projects/
  cge/              (CGE docs — this repository)
  context-service/  (the example project)
```

`CGE_ROOT` in `run-workflow.sh` resolves to `../cge` relative to `context-service/`.

#### Running the workflow

```bash
cd /path/to/context-service
bash run-workflow.sh
```

The workflow runs four phases sequentially. Each phase invokes Claude via `claude --dangerously-skip-permissions`. Claude writes artifacts and source files directly into the project directory.

Watch Phase 4 (longest — 7 tasks):
```bash
tail -f .cge/runs/implementation/execute-work-plan-*/logs/*.log
```

#### Expected outputs after completion

```text
context-service/
  pom.xml
  src/main/java/com/set4d/context/
    ContextServiceApplication.java
    config/SecurityConfig.java
    controller/ContextController.java
    service/ContextService.java
    service/ContextEnrichmentException.java
    client/PreferencesClient.java
    dto/ContextRequest.java
    dto/ContextResponse.java
    dto/ExternalPreferencesResponse.java
  src/main/resources/application.yml
  src/test/java/com/set4d/context/
    service/ContextServiceTest.java
    controller/ContextControllerTest.java
  .cge/runs/
    analysis/define-project-context-YYYYMMDD-001/
      artifacts/project_context/project_context.json
      audits/define-project-context.audit.json
    design/decompose-feature-YYYYMMDD-001/
      artifacts/decomposition_report/decomposition_report.json
    design/plan-service-or-feature-YYYYMMDD-001/
      artifacts/work_plan/work_plan.json
    implementation/execute-work-plan-YYYYMMDD-001/
      audits/implement-*.audit.json  (7 files)
      reports/run_report.json
      reports/drift_report.json
```

Verify the build:
```bash
cd context-service
mvn test
```

All tests should pass.

#### Partial re-run example

To re-run only the adapters task after making a change:

```bash
PLAN=$(find .cge/runs/design -name "work_plan.json" | sort | tail -1)
PREV_RUN=$(ls -d .cge/runs/implementation/execute-work-plan-* | sort | tail -1 | xargs basename)

bash ../cge/runners/workflows/execute-work-plan.sh \
  --project-path . \
  --work-plan-ref "${PLAN}" \
  --only-tasks implement-adapters-and-integrations \
  --rerun-of "implementation/${PREV_RUN}"
```

### 17.9 Example canonical artifacts

See `cge/examples/end_to_end/artifacts/` for reference JSON for each phase:

| File | Phase | Description |
|------|-------|-------------|
| `project_context.example.json` | analysis | Structured project understanding |
| `decomposition_report.example.json` | design | 7 bounded work candidates |
| `work_plan.example.json` | design | Ordered task plan with prompt_refs |
| `task_audit.example.json` | implementation | Evidence from implement-service |
| `run_report.example.json` | implementation | 7/7 tasks passed |
| `drift_report.example.json` | implementation | PreferencesService fallback signal |
| `actionable_output.example.json` | implementation | Story: add circuit breaker |

All `run_id` values in these examples follow the format `{phase}/{task-label}-{YYYYMMDD}-{NNN}`.

## Appendix A: Minimal requirements checklist

A project repository is ready for CGE execution when:

* `.cge/feature-request.md` exists with the feature or service description
* `.cursorrules` exists at project root with CGE global context reference
* `run-workflow.sh` exists and chains the four convenience wrappers
* CGE docs repository is accessible at `../cge` (sibling directory) or CGE_DOCS_ROOT is set
* `yq`, `jq`, and `claude` CLI are installed

## Appendix B: Worked example — context-service

The complete end-to-end worked example is in `cge/examples/end_to_end/`. It demonstrates:

* feature request intake → `feature-request.md`
* project context definition → `project_context.example.json`
* feature decomposition → `decomposition_report.example.json`
* work plan creation → `work_plan.example.json`
* ordered implementation → `task_audit.example.json`
* run reporting → `run_report.example.json`
* drift signal derivation → `drift_report.example.json`
* actionable output generation → `actionable_output.example.json`

See Section 17.8 for the complete run instructions and expected outputs. See `cge/examples/end_to_end/README.md` for the command-by-command walkthrough.

### Appendix B.1 Why this worked example matters

This end-to-end example demonstrates that CGE is not only a task runner. It is an operating model that turns execution into evidence, evidence into reports, and reports into system improvement. The context-service example builds a complete Spring Boot service from a single `feature-request.md` seed file, with every artifact and source file written by Claude under governed bounded task execution.

# Annex A: SDLC Phase Taxonomy

## A.1 Authority and status

This annex is normative.

## A.2 Purpose

This annex defines the canonical SDLC phase taxonomy used by CGE for tasks, workflows, audits, reports, and conformance artifacts.

## A.3 Canonical values

The canonical `sdlc_phase` values are:

* `analysis`
* `design`
* `implementation`
* `testing`
* `release_ops`

## A.4 Definitions and applicability

### `analysis`

Purpose:

* establish understanding of problem domain, responsibilities, constraints, vocabulary, decomposition, and scope

Typical outputs:

* project context
* decomposition report
* responsibility map
* domain vocabulary artifact

### `design`

Purpose:

* define intentional solution structure, decisions, contracts, and interaction patterns before implementation

Typical outputs:

* API contracts
* architectural decision records
* data model artifacts
* integration interaction specifications

### `implementation`

Purpose:

* perform bounded code and configuration changes that realize approved design or approved change scope

Typical outputs:

* implementation artifacts by conceptual boundary
* code change evidence
* implementation audits

### `testing`

Purpose:

* define, implement, or execute tests and produce evidence about correctness, quality, and conformance

Typical outputs:

* test strategy artifacts
* unit or integration test assets
* contract test assets
* test execution evidence

### `release_ops`

Purpose:

* define and validate operational readiness, deployment readiness, runtime controls, and observability for deployable systems

Typical outputs:

* deployment configuration artifacts
* runtime policy artifacts
* readiness reports
* monitoring and operational artifacts

## A.5 Taxonomy usage rules

1. Every task MUST declare exactly one `sdlc_phase`.
2. Every workflow MUST declare exactly one `sdlc_phase`.
3. Every task conceptual boundary MUST come from the approved boundary taxonomy for the declared phase.
4. Profiles MAY specialize boundaries or execution behavior but MUST NOT redefine canonical phases.

---

# Annex B: Conceptual Boundary Taxonomy by SDLC Phase

## B.1 Authority and status

This annex is normative.

## B.2 Purpose

This annex defines the canonical conceptual boundary taxonomy used to bound tasks within each SDLC phase.

## B.3 General rules

1. A task MUST declare exactly one conceptual boundary.
2. A conceptual boundary MUST be valid for the task's declared `sdlc_phase`.
3. Profiles MAY define specialized boundaries only when they map explicitly to a canonical boundary in this annex.
4. When a specialized boundary is used, audits and reports MUST record both the specialized identifier and the canonical mapped identifier.

## B.4 Canonical boundaries by phase

### B.4.1 Analysis boundaries

Canonical `analysis` boundaries:

* `domain_vocabulary`
* `responsibilities`
* `bounded_context`
* `data_flow`
* `feature_decomposition`
* `constraint_model`
* `integration_inventory`

Boundary descriptions:

* `domain_vocabulary`: terms, definitions, and semantic normalization
* `responsibilities`: purpose, scope, and ownership of capabilities
* `bounded_context`: domain boundary separation and interaction seams
* `data_flow`: movement, transformation, and lifecycle of data
* `feature_decomposition`: decomposition of requested work into bounded units
* `constraint_model`: policy, operational, architectural, and business constraints
* `integration_inventory`: external systems, dependencies, and interaction surface inventory

### B.4.2 Design boundaries

Canonical `design` boundaries:

* `api_contract`
* `interaction_design`
* `data_model`
* `decision_record`
* `integration_design`
* `security_design`
* `observability_design`

Boundary descriptions:

* `api_contract`: externally visible interface definitions and expectations
* `interaction_design`: internal or external interaction flow and collaboration design
* `data_model`: persistent, transfer, or canonical data structure design
* `decision_record`: governed architectural or design decision
* `integration_design`: contracts or behavior for dependent systems and adapters
* `security_design`: authorization, authentication, secrets, and control design
* `observability_design`: telemetry, logging, tracing, metrics, and alerting design

### B.4.3 Implementation boundaries

Canonical `implementation` boundaries:

* `api_contract`
* `dto`
* `controller`
* `service`
* `repository`
* `messaging_and_events`
* `adapters_and_integrations`
* `configuration`
* `observability`
* `error_handling`
* `security_controls`

Boundary descriptions:

* `api_contract`: implementation of interface contract artifacts or public contract definitions
* `dto`: transfer models and serialization-bound structures
* `controller`: request handling or endpoint entrypoint logic
* `service`: business orchestration and core application behavior
* `repository`: persistence interaction and data access logic
* `messaging_and_events`: message publication, consumption, routing, or event handling behavior
* `adapters_and_integrations`: external client adapters or integration-facing code
* `configuration`: application configuration and environment binding behavior
* `observability`: implementation of metrics, logging, tracing, and runtime instrumentation
* `error_handling`: exception mapping, failure semantics, and resilience behavior
* `security_controls`: code-level enforcement of authorization, authentication, and security policy

### B.4.4 Testing boundaries

Canonical `testing` boundaries:

* `test_strategy`
* `test_unit`
* `test_integration`
* `test_contract`
* `test_e2e`
* `test_performance`
* `test_security`

Boundary descriptions:

* `test_strategy`: test planning and coverage intent
* `test_unit`: unit-level tests of isolated components
* `test_integration`: integration tests across collaborating components
* `test_contract`: contract validation across consumers and providers
* `test_e2e`: end-to-end validation through system flows
* `test_performance`: performance and scalability evidence
* `test_security`: security-focused validation artifacts and evidence

### B.4.5 Release and operations boundaries

Canonical `release_ops` boundaries:

* `deployment_config`
* `runtime_policy`
* `operational_readiness`
* `monitoring`
* `incident_operability`
* `release_evidence`

Boundary descriptions:

* `deployment_config`: deployable runtime packaging and environment configuration
* `runtime_policy`: operational controls, limits, and environment guardrails
* `operational_readiness`: readiness review and operational capability evidence
* `monitoring`: dashboards, alerts, and runtime observation assets
* `incident_operability`: incident response hooks, playbooks, and operability evidence
* `release_evidence`: evidence supporting release confidence and governance approval

## B.5 Example profile mappings

Examples of specialized-to-canonical mappings:

* `camel_routes` -> `messaging_and_events`
* `web_client` -> `adapters_and_integrations`
* `postgres_repo` -> `repository`
* `spring_security` -> `security_controls`
* `otel_instrumentation` -> `observability`

## B.6 Enforcement implications

Profiles governing code-modifying tasks MUST define:

* allowed path declaration format
* disallowed path declaration format
* enforcement mechanism used by runners or gates
* failure behavior when file-scope violations occur

---

# Annex C: Project Type Taxonomy and Applicability

## C.1 Authority and status

This annex is normative.

## C.2 Purpose

This annex defines canonical project types and the applicability of CGE workflows, boundaries, and controls by project type.

## C.3 Canonical project types

Canonical `project_type` values are:

* `spring_boot_app`
* `java_lambda`
* `java_commons_lib`
* `pom_parent`
* `cross_cutting`

## C.4 Definitions and applicability

### `spring_boot_app`

Definition:

* deployable Spring Boot application or service

Typical applicable boundaries:

* all `analysis` boundaries
* most `design` boundaries
* implementation boundaries including `controller`, `service`, `repository`, `messaging_and_events`, `adapters_and_integrations`, `configuration`, `observability`, `error_handling`, `security_controls`
* testing and release operations boundaries

### `java_lambda`

Definition:

* deployable Java runtime intended for Lambda or equivalent event-driven execution surface

Typical applicable boundaries:

* `analysis`, `design`, `testing`, `release_ops`
* implementation boundaries commonly include `service`, `adapters_and_integrations`, `messaging_and_events`, `configuration`, `observability`, `error_handling`, `security_controls`
* `controller` is usually not applicable unless profile explicitly maps an entrypoint model to it

### `java_commons_lib`

Definition:

* shared Java library or reusable code module not independently deployed as an application

Typical applicable boundaries:

* `analysis`, `design`, `implementation`, `testing`
* `release_ops` applicability is typically limited to publication and release evidence
* `controller` and some deployment-oriented boundaries are usually not applicable

### `pom_parent`

Definition:

* parent POM or build-governance repository that governs dependency and plugin behavior for inheriting projects

Typical applicable boundaries:

* `analysis`, `design`, `implementation`, `testing`, limited `release_ops`
* implementation focus usually centers on `configuration`, `security_controls`, `observability` where build enforcement applies
* operational boundaries apply only where release evidence or publication governance exists

### `cross_cutting`

Definition:

* repository, package, or governed asset set whose primary purpose is standards, governance, or common reusable rules across projects

Typical applicable boundaries:

* all analysis and design boundaries
* selected implementation boundaries where executable enforcement assets exist
* selected release and testing boundaries where governance evidence is produced

## C.5 Applicability rules

1. Every project context MUST declare exactly one canonical `project_type`.
2. Profiles MAY add specialized project type descriptors only when they map to one canonical `project_type`.
3. A workflow SHOULD validate applicability of task boundaries against project type.
4. If a task boundary is not applicable for the declared project type, the workflow MUST fail or require an approved exception.

---

# Annex D: Artifact Schema Governance and Schema Catalog

## D.1 Authority and status

This annex is normative.

## D.2 Purpose

This annex defines how artifact schemas are governed, versioned, cataloged, and referenced.

## D.3 General rules

1. Every ASA type MUST have a governed schema.
2. Every schema MUST have:

   * `schema_id`
   * `schema_version`
   * owning governance tier
   * status
   * compatibility policy
3. Every ASA instance MUST declare `schema_id` and `schema_version`.
4. Schema validation MUST evaluate the canonical JSON representation.

## D.4 Schema catalog structure

The schema catalog MUST include, at minimum:

* artifact schemas
* audit schemas
* report schemas
* validation rule schemas
* actionable output schemas

A registry entry SHOULD include:

* `schema_id`
* `schema_version`
* `artifact_class`
* `title`
* `status`
* `owner`
* `compatibility`
* `supersedes`
* `effective_date`

## D.5 Required baseline artifact schemas

The CGE baseline schema catalog MUST include, at minimum:

* `artifact.project_context`
* `artifact.decomposition_report`
* `artifact.work_plan`
* `artifact.actionable_output`
* `artifact.run_manifest`
* `artifact.project_structure`
* `audit.task_audit`
* `report.run_report`
* `report.repository_report`
* `report.system_report`
* `report.catalogue_report`
* `report.drift_report`

## D.6 Compatibility and change policy

1. Backward-compatible schema changes SHOULD increment the minor version.
2. Breaking schema changes MUST increment the major version.
3. Deprecation status MUST be recorded in the schema registry before removal.
4. Validation tools MUST use the declared schema version, not an implicit latest version.

## D.7 Schema governance lifecycle

A schema lifecycle SHOULD include:

* `draft`
* `approved`
* `deprecated`
* `retired`

Only `approved` schemas MAY be used for required conformance evidence unless an exception allows otherwise.

## D.8 Example registry entry

```json
{
  "schema_id": "artifact.work_plan",
  "schema_version": "1.0.0",
  "artifact_class": "asa",
  "title": "Bounded Work Plan",
  "status": "approved",
  "owner": "tier1.governance",
  "compatibility": "semantic_versioning",
  "supersedes": null,
  "effective_date": "2026-03-05"
}
```

---

# Annex E: Audit Schema Specification

## E.1 Authority and status

This annex is normative.

## E.2 Purpose

This annex defines the minimum required schema and semantic expectations for CGE task audits.

## E.3 Required schema

Canonical audit schema:

* `audit.task_audit`

Required version baseline:

* `1.0.0`

## E.4 Required fields

A conformant `audit.task_audit` ASA MUST include, at minimum:

* `schema_id`
* `schema_version`
* `audit_id`
* `timestamp`
* `run_id`
* `workflow_id`
* `task_id`
* `sdlc_phase`
* `conceptual_boundary`
* `provenance`
* `inputs`
* `outputs`
* `summary`
* `work_performed`
* `gate_outcomes`
* `assumptions`
* `risks`
* `confidence`
* `exceptions_used`

## E.5 Provenance requirements

The `provenance` object MUST include, at minimum:

* `cge_spec_version`
* `governing_context_id`
* `work_state_id`
* `workflow_definition_id`, when applicable
* `runner_id`, when applicable
* `profile_ids`, when applicable

## E.6 Input and output references

1. Inputs and outputs MAY be represented as references.
2. References SHOULD include type and stable path or identifier.
3. If external inputs are used, the audit SHOULD record source identity sufficient for review and reproducibility.

## E.7 Confidence values

Allowed canonical `confidence` values are:

* `high`
* `medium`
* `low`

Governance MAY define phase-specific thresholds and escalation rules.

## E.8 Example audit envelope

```json
{
  "schema_id": "audit.task_audit",
  "schema_version": "1.0.0",
  "audit_id": "audit-20260305-001",
  "timestamp": "2026-03-05T18:00:00Z",
  "run_id": "run-20260305-180000",
  "workflow_id": "analysis.feature-intake-and-decomposition",
  "task_id": "decompose-feature",
  "sdlc_phase": "analysis",
  "conceptual_boundary": "feature_decomposition",
  "provenance": {
    "cge_spec_version": "1.0.0",
    "governing_context_id": "set4d/docs@<DOCS_SHA>",
    "work_state_id": "set4d/app/example-service@<REPO_SHA>",
    "workflow_definition_id": "analysis.feature-intake-and-decomposition@1.0.0",
    "runner_id": "profile.set4d.gitlab_shell_claude@1.0.0:cge-run.sh"
  },
  "inputs": [
    {"type": "project_context", "ref": ".cge/runs/run-20260305-175000/artifacts/project_context/project_context.json"},
    {"type": "feature_request", "ref": "inputs/feature_request.md"}
  ],
  "outputs": [
    {"type": "decomposition_report", "ref": ".cge/runs/run-20260305-180000/artifacts/decomposition_report/decomposition_report.json"}
  ],
  "summary": "Produced bounded feature decomposition for approved feature request.",
  "work_performed": [
    "Loaded project context and referenced global context",
    "Derived bounded task candidates",
    "Produced decomposition report ASA"
  ],
  "gate_outcomes": [
    {"gate_id": "schema_validate", "result": "pass"}
  ],
  "assumptions": [],
  "risks": [],
  "confidence": "high",
  "exceptions_used": []
}
```

## E.9 Vibe session audit schema

CGE provides a canonical audit schema for governed exploratory vibe sessions:

* `audit.vibe_session_audit`

This schema serves a distinct purpose from `audit.task_audit`:

1. `audit.task_audit` remains the mandatory audit for bounded tasks within a single conceptual boundary.
2. `audit.vibe_session_audit` is used for governed exploratory vibe sessions that may explore multiple candidate boundaries during decomposition, planning, review, or remediation activities.
3. If a vibe session results in formal bounded execution, required task audits still apply.

Vibe session audits MUST record:

* session mode (exploration, decomposition, planning, drafting, review, remediation)
* candidate boundaries touched
* exploration status (remained exploratory, crossed into governed execution, or produced candidate governed work)
* confidence level
* whether outputs require reconciliation through bounded tasks
* follow-up recommendations

Vibe session audits are authoritative as evidence that the session occurred but are not by themselves authoritative engineering artifacts for implementation, testing, or release work unless validated through standard CGE workflows.

---

# Annex F: Report Schema Set and Minimum Report Requirements

## F.1 Authority and status

This annex is normative.

## F.2 Purpose

This annex defines the minimum report schema set, the report envelope, and minimum report requirements for conformance.

## F.3 Canonical report schema set

The minimum canonical report schema set is:

* `report.run_report`
* `report.repository_report`
* `report.system_report`
* `report.catalogue_report`
* `report.drift_report`

## F.4 Common report envelope

Every report ASA MUST include, at minimum:

* `schema_id`
* `schema_version`
* `report_id`
* `report_type`
* `generated_at`
* `scope`
* `time_window`
* `source_refs`
* `summary`
* `findings`
* `generated_by`

## F.5 Report type minimum requirements

### `report.run_report`

Purpose:

* summarize one workflow execution run

Minimum additional fields:

* `run_id`
* `workflow_id`
* `task_results`
* `gate_summary`
* `artifact_index`

### `report.repository_report`

Purpose:

* summarize a repository over a defined time window

Minimum additional fields:

* `repository_id`
* `run_count`
* `gate_failure_summary`
* `drift_signal_summary`
* `actionable_output_candidates`

### `report.system_report`

Purpose:

* summarize system-wide governance state across repositories or scopes

Minimum additional fields:

* `system_scope`
* `conformance_summary`
* `profile_usage_summary`
* `cross_repo_drift_summary`
* `priority_actions`

### `report.catalogue_report`

Purpose:

* summarize workflow, task, schema, or policy catalogue health

Minimum additional fields:

* `catalogue_scope`
* `asset_status_summary`
* `deprecated_asset_summary`
* `coverage_gaps`

### `report.drift_report`

Purpose:

* summarize observed drift signals and recommended governance actions

Minimum additional fields:

* `signal_counts`
* `signal_clusters`
* `repeat_patterns`
* `recommended_actions`
* `linked_actionable_outputs`

## F.6 Minimum reporting obligations by conformance level

* **L1**: no periodic reporting requirement beyond required task audits
* **L2**: every workflow run MUST produce `report.run_report`
* **L3**: governance cadence MUST produce repository, system, or drift reports as defined by scope policy

## F.7 Drift signal minimum categories

A drift-capable reporting system SHOULD detect, at minimum:

* repeated assumptions
* repeated risks
* low-confidence clusters
* recurring gate failures
* policy conflict patterns
* rework patterns

## F.8 Actionable output derivation

A report MAY derive `feature` or `story` actionable outputs. Derived outputs MUST include evidence references to the source report and underlying audits or artifacts.

---

# Annex G: Approved Profiles and Their Applicability

## G.1 Authority and status

This annex is normative.

## G.2 Purpose

This annex defines the approved profile catalog and the applicability of each approved profile.

## G.3 Profile rules

1. Every enabled profile MUST have a unique identifier and version.
2. Every approved profile MUST define:

   * scope
   * environment or execution surface
   * mandatory controls beyond base specification behavior
   * applicability conditions
3. A profile MUST NOT weaken the CGE Specification.

## G.4 Approved profiles

### `profile.set4d.git.identity@1.0.0`

Purpose:

* standardize immutable Git-based provenance references for governing context and work state

Applicability:

* any set4d repository using Git-based evidence attribution

Required behavior:

* audits MUST record docs repo commit identity when global context is sourced from Git
* audits MUST record target repo commit identity for work state attribution

### `profile.set4d.ci.gates@1.0.0`

Purpose:

* standardize gate execution in CI and merge request enforcement

Applicability:

* repositories participating in governed CI execution

Required behavior:

* required gates MUST fail CI on failure
* evidence artifacts MUST be retained as CI artifacts per policy
* required gate outcomes MUST be visible to merge request reviewers

### `profile.set4d.gitlab_shell_claude@1.0.0`

Purpose:

* define the primary set4d execution profile using GitLab, shell runners, and Claude Code

Applicability:

* set4d repositories executing bounded tasks through shell runners with Claude Code as the AI execution surface

Required behavior:

* workflows implementing feature work MUST support a plan-first mode
* ordered execution workflows MUST execute tasks in declared order
* task execution MUST occur as fresh stateless invocation per task
* code-modifying tasks MUST declare allowed and disallowed modification surfaces
* partial re-runs MUST create new runs and new evidence outputs
* runners MUST materialize resolved prompt files for provenance when prompts are runner-generated

### `profile.set4d.shared_foundations@1.0.0`

Purpose:

* apply stronger controls to governance surfaces such as parent POM and shared Spring foundations

Applicability:

* repositories classified as `pom_parent`, `java_commons_lib`, or explicitly designated governance surfaces

Required behavior:

* changes SHOULD require extended gates
* governance MAY require additional reviewers or additional evidence thresholds

## G.5 Profile applicability rules

1. A conformance claim MUST list all enabled profiles.
2. If multiple profiles apply, their combined behavior MUST remain non-contradictory.
3. If profile obligations conflict, the stricter obligation applies unless an approved exception exists.
4. Project context SHOULD declare applicable profiles in `references.json`.

## G.6 Example profile declaration in project context references

```json
{
  "schema_id": "artifact.references",
  "schema_version": "1.0.0",
  "project": {
    "name": "example-service",
    "group_path": "set4d/app/example-service"
  },
  "global_context": {
    "docs_repo_path": "set4d/docs",
    "global_context_version": "1.0.0",
    "global_context_commit": "<DOCS_SHA>"
  },
  "profiles_enabled": [
    "profile.set4d.git.identity@1.0.0",
    "profile.set4d.ci.gates@1.0.0",
    "profile.set4d.gitlab_shell_claude@1.0.0"
  ]
}
```

---

# Document H: Multi-Source Code Decomposition for Modernization

## H.1 Overview

CGE supports multi-source code decomposition for C++ → Spring Boot modernization projects. This capability extracts structured knowledge from four complementary sources:

1. **Legacy C++ Code** - Algorithms, mathematical formulas, preservation priorities
2. **Production API** - REST contracts that MUST be preserved
3. **Modernization Attempt** - Lessons learned (data strategy, what worked/didn't)
4. **Verification Tests** - Golden master test infrastructure

## H.2 New Schemas

This implementation adds 11 new schemas to support multi-source decomposition:

**C++ Schemas** (4):
- `cpp.algorithm@1.0.0` - Algorithm documentation with mathematical operations
- `cpp.file_inventory@1.0.0` - File classification and dependencies
- `cpp.class_inventory@1.0.0` - Class structure and relationships
- `cpp.build_system@1.0.0` - Build configuration and external dependencies

**Spring Boot API Schemas** (2):
- `springboot.api_contract@1.0.0` - REST API contracts to preserve
- `springboot.dto_contract@1.0.0` - Data Transfer Objects

**Modernization Schemas** (3):
- `modernization.data_strategy@1.0.0` - HDF5 → JSON migration strategy
- `modernization.algorithm_implementation@1.0.0` - Implementation lessons
- `modernization.domain_model@1.0.0` - Domain entities

**Verification Schemas** (3):
- `verification.test_suite@1.0.0` - Test suite structure
- `verification.io_file_format@1.0.0` - Legacy I/O file formats
- `verification.tolerance_spec@1.0.0` - Numerical accuracy tolerances

## H.3 Workflows

Master workflow runs all four decompositions:

```bash
./runners/workflows/decompose-for-modernization.sh \
  --cpp-codebase /path/to/cpp/project \
  --springboot-codebase /path/to/production/api \
  --modern-codebase /path/to/modernization/attempt \
  --verification-codebase /path/to/tests \
  --project-path /path/to/working/dir
```

Individual decomposition workflows also available:
- `decompose-cpp-legacy.sh`
- `decompose-production-api.sh`
- `decompose-modern-attempt.sh`
- `decompose-verification-tests.sh`

## H.4 Preservation Priorities

| Priority | Criteria | Examples |
|----------|----------|----------|
| **CRITICAL** | Validated against observations, precision < 10^-12 | Scientific algorithms, deterministic RNG |
| **HIGH** | Core domain algorithms, coordinate transforms | Physics models, statistical distributions |
| **MEDIUM** | Helper algorithms, orchestration | Optimization, control flow |
| **LOW** | Utilities, formatting | String manipulation, logging |

## H.5 Output Structure

```
.cge/runs/analysis/decompose-for-modernization-YYYYMMDD-NNN/
├── artifacts/
│   ├── cpp_decomposition/        # Algorithms, files, classes, build
│   ├── api_decomposition/        # API contracts, DTOs
│   ├── modernization/            # Data strategy, lessons, domain model
│   └── verification/             # Test suite, I/O formats, tolerances
├── audits/                       # One audit per decomposition
└── reports/                      # Cost metrics
```

## H.6 Documentation

For C++ to Spring Boot modernization workflows, see:
- [Project Initialization and Execution Guide](docs/workflows/project_initialization_and_execution.md) - Complete walkthrough (Scenario 2)
- Detailed schema descriptions in `schemas/` directory
- Algorithm extraction best practices in workflow prompts
- Example workflows using `cpp-decomposition.sh` wrapper

## H.7 Integration with Existing CGE

The multi-source decomposition:
- Extends CGE's analysis phase capabilities
- Follows existing schema registry patterns
- Uses standard workflow orchestration
- Integrates with cost tracking and validation
- Maintains backward compatibility with existing workflows

---
