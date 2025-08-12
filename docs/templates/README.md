# Document Templates

This directory contains Amazon-style document templates that support disciplined decision-making and development processes for the Redweed project.

## Available Templates

### Strategic and Planning Documents

#### [6-Pager](./6-pager.md)
For major strategic decisions and comprehensive proposals that require extensive analysis. These narrative documents are used for significant initiatives that impact the product direction, architecture, or business strategy.

**When to use:** Major feature development, architectural changes, strategic pivots, resource allocation decisions.

#### [1-Pager](./1-pager.md)
For smaller initiatives and quick decisions that don't require extensive analysis. Focuses on clarity and actionability.

**When to use:** Small features, process improvements, quick experiments, tactical decisions.

#### [Press Release and FAQ (PR/FAQ)](./pr-faq.md)
Working backwards approach where teams write a mock press release and FAQ before building a product. Helps clarify customer benefit and potential issues upfront.

**When to use:** New features, product launches, major updates that affect user experience.

#### [Working Backwards Document](./working-backwards.md)
Comprehensive customer experience narratives that describe the ideal customer journey before building the solution.

**When to use:** Major new features, user experience redesigns, customer-facing improvements.

### Technical Documents

#### [Architecture Decision Record (ADR)](./adr.md)
Captures important architectural decisions, including context, options considered, and rationale. Serves as historical record for future developers.

**When to use:** Significant technology choices, architectural patterns, technical trade-offs.

#### [Design Document](./design-document.md)
Technical specifications that outline system architecture, APIs, data models, and implementation approaches. More detailed than ADRs and focuses on the "how" of implementation.

**When to use:** Complex feature implementation, system integrations, API design.

### Operational Documents

#### [Operational Runbook](./operational-runbook.md)
Describes how to operate, monitor, troubleshoot, and maintain systems in production. Critical for "you build it, you run it" culture.

**When to use:** Production systems, deployment procedures, maintenance tasks.

#### [Post-Incident Report (PIR)](./post-incident-report.md)
Detailed analyses of outages or issues, focusing on root causes, timeline, and preventive measures. Emphasizes learning from failures rather than blame.

**When to use:** After any significant incident, outage, or operational issue.

## Usage Guidelines

### Document Selection
Choose the appropriate template based on:
- **Scope and Impact:** Larger decisions require more comprehensive documents
- **Audience:** Consider who needs to review and approve
- **Timeline:** Some templates support quick decisions, others require thorough analysis
- **Purpose:** Strategic planning vs. technical implementation vs. operational procedures

### Writing Principles
1. **Narrative over Bullets:** Use clear, flowing prose rather than bullet points where possible
2. **Customer Focus:** Always consider the customer impact and value
3. **Data-Driven:** Support arguments with evidence and metrics
4. **Clear Reasoning:** Explain not just what, but why
5. **Actionable Outcomes:** Every document should lead to clear next steps

### Review Process
1. **Silent Reading:** Begin meetings by silently reading the document
2. **Structured Discussion:** Use the document structure to guide conversation
3. **Decision Tracking:** Clearly capture decisions and action items
4. **Follow-up:** Track implementation of decisions made

### Document Lifecycle
1. **Draft:** Initial version for internal review
2. **Under Review:** Shared with stakeholders for feedback
3. **Approved:** Finalized and ready for implementation
4. **Implemented:** Solution is built and deployed
5. **Archived/Superseded:** Document is replaced or no longer relevant

## Integration with Development Workflow

### Planning Phase
- Start with **Working Backwards** or **PR/FAQ** for customer-facing features
- Use **6-pager** for major initiatives requiring comprehensive planning
- Use **1-pager** for smaller improvements and experiments

### Design Phase
- Create **Design Documents** for technical implementation
- Write **ADRs** for significant architectural decisions
- Reference planning documents to ensure alignment with customer value

### Implementation Phase
- Update design documents as implementation details emerge
- Create **Operational Runbooks** for production systems
- Document deployment and monitoring procedures

### Operations Phase
- Use **Operational Runbooks** for day-to-day operations
- Write **Post-Incident Reports** after any issues
- Update documentation based on operational learnings

### Continuous Improvement
- Review completed documents to assess decision quality
- Update templates based on team learnings
- Share successful document examples within the team

## Template Customization

These templates can be adapted for the Redweed project's specific needs:
- Remove sections that aren't relevant
- Add project-specific sections as needed
- Adjust depth of detail based on team size and process
- Customize language and tone for your team culture

## Related Resources

- **Main Documentation:** [../README.md](../README.md)
- **Architecture Documentation:** [../architecture.md](../architecture.md)
- **API Documentation:** [../API.md](../API.md)
- **Development Guidelines:** [../../.github/copilot-instructions.md](../../.github/copilot-instructions.md)

---

*These templates are inspired by Amazon's document-driven culture and adapted for the Redweed personal information management system.*