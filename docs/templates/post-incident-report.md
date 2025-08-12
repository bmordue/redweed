# Post-Incident Report (PIR) Template

**Document Type:** Post-Incident Report  
**Incident ID:** [Unique identifier]  
**Date of Incident:** YYYY-MM-DD  
**Date of Report:** YYYY-MM-DD  
**Report Author:** [Your Name]  
**Incident Commander:** [Name]  
**Reviewers:** [List of reviewers]

## Executive Summary

*2-3 sentence summary of what happened, the impact, and key learnings*

## Incident Details

### Basic Information
- **Incident Start Time:** [YYYY-MM-DD HH:MM UTC]
- **Incident End Time:** [YYYY-MM-DD HH:MM UTC]
- **Total Duration:** [Duration]
- **Detection Time:** [How long to detect]
- **Resolution Time:** [How long to resolve]

### Severity Classification
- **Severity Level:** [Critical/High/Medium/Low]
- **Impact:** [Description of business/user impact]
- **Affected Systems:** [List of impacted services/components]
- **Affected Users:** [Number or percentage of users impacted]

### Service Level Impact
| Service | SLA Target | Actual Performance | Impact Duration |
|---------|------------|-------------------|-----------------|
| | | | |
| | | | |

## Timeline of Events

*Detailed chronological timeline of the incident*

| Time (UTC) | Event | Actor | Action Taken |
|------------|-------|-------|--------------|
| HH:MM | [Event description] | [Person/System] | [Action] |
| HH:MM | [Event description] | [Person/System] | [Action] |
| HH:MM | [Event description] | [Person/System] | [Action] |
| HH:MM | [Event description] | [Person/System] | [Action] |

### Key Timeline Milestones
- **Incident Start:** [Time and description]
- **Detection:** [Time and how it was detected]
- **First Response:** [Time and initial response]
- **Root Cause Identified:** [Time when root cause was found]
- **Fix Implemented:** [Time when fix was deployed]
- **Service Restored:** [Time when service was fully operational]
- **All Clear:** [Time when incident was declared resolved]

## Root Cause Analysis

### What Happened
*Clear, factual description of what occurred*

### Root Cause
*The fundamental reason why the incident occurred*

### Contributing Factors
*Other factors that made the incident possible or worse*
- 
- 
- 

### How the Issue Was Introduced
*When and how the root cause was introduced into the system*

### Why It Wasn't Caught Earlier
*What detection mechanisms failed or were missing*

## Impact Assessment

### User Impact
- **Number of Users Affected:** [Count or percentage]
- **User Experience Impact:** [Description of how users were affected]
- **Geographic Impact:** [Which regions were affected]

### Business Impact
- **Revenue Impact:** [Estimated financial impact]
- **Operational Impact:** [Impact on business operations]
- **Reputation Impact:** [Customer satisfaction, media coverage]

### Technical Impact
- **System Performance:** [How system performance was affected]
- **Data Integrity:** [Any data corruption or loss]
- **Security Impact:** [Any security implications]

### Metrics and Data
| Metric | Normal Value | During Incident | Peak Impact |
|--------|--------------|-----------------|-------------|
| | | | |
| | | | |

## Response Analysis

### What Went Well
*Positive aspects of the incident response*
- 
- 
- 

### What Could Be Improved
*Areas where the response could have been better*
- 
- 
- 

### Communication Analysis
*How well stakeholders were informed*

#### Internal Communication
- **Notification Time:** [How quickly teams were alerted]
- **Communication Channels:** [What channels were used]
- **Information Quality:** [How accurate and timely updates were]

#### External Communication
- **Customer Notification:** [How and when customers were informed]
- **Status Page Updates:** [Timeline of public updates]
- **Support Team Briefing:** [How support was informed]

### Tools and Procedures
*How well existing tools and procedures worked*

#### Monitoring and Alerting
- **Detection Effectiveness:** [How well monitoring detected the issue]
- **Alert Quality:** [Were alerts clear and actionable]
- **False Positives/Negatives:** [Any issues with alerting]

#### Incident Response Process
- **Process Adherence:** [How well the incident response process was followed]
- **Role Clarity:** [Were roles and responsibilities clear]
- **Decision Making:** [How effectively decisions were made]

## Action Items

### Immediate Actions (Complete within 24-48 hours)
| Action | Owner | Due Date | Status |
|--------|-------|----------|--------|
| | | | |
| | | | |

### Short-term Actions (Complete within 1-2 weeks)
| Action | Owner | Due Date | Status |
|--------|-------|----------|--------|
| | | | |
| | | | |

### Long-term Actions (Complete within 1-3 months)
| Action | Owner | Due Date | Status |
|--------|-------|----------|--------|
| | | | |
| | | | |

### Process Improvements
*Changes to prevent similar incidents*
- 
- 
- 

### Technical Improvements
*System changes to improve reliability*
- 
- 
- 

### Monitoring Improvements
*Better detection and alerting*
- 
- 
- 

## Lessons Learned

### Key Takeaways
*Most important lessons from this incident*
1. 
2. 
3. 

### Knowledge Gaps Identified
*Areas where team knowledge was lacking*
- 
- 
- 

### Documentation Updates Needed
*What documentation needs to be created or updated*
- 
- 
- 

### Training Needs
*Training that would help prevent or better handle similar incidents*
- 
- 
- 

## Prevention Strategies

### System Improvements
*Technical changes to prevent recurrence*
- 
- 
- 

### Process Improvements
*Operational changes to prevent recurrence*
- 
- 
- 

### Monitoring Enhancements
*Better observability to catch issues earlier*
- 
- 
- 

### Testing Improvements
*Better testing to catch issues before production*
- 
- 
- 

## Related Incidents

### Similar Past Incidents
*Previous incidents with similar characteristics*
| Date | Incident ID | Similarity | Lessons |
|------|-------------|------------|---------|
| | | | |
| | | | |

### Pattern Analysis
*Any patterns this incident is part of*
- 
- 

## Supporting Data

### Log Excerpts
*Key log entries that helped with diagnosis*
```
[Timestamp] [Level] [Component] [Message]
```

### Metrics and Graphs
*Links to relevant dashboards and metric snapshots*
- 
- 

### External References
*Links to vendor documentation, bug reports, etc.*
- 
- 

## Follow-up Actions

### Review Schedule
- **1 Week Follow-up:** [Date] - Check immediate action completion
- **1 Month Follow-up:** [Date] - Review short-term action progress
- **3 Month Follow-up:** [Date] - Assess long-term improvements

### Success Metrics
*How we'll measure if our improvements worked*
- 
- 
- 

### Next Review Date
*When this report should be reviewed again*
**Date:** [YYYY-MM-DD]

---

**Instructions for Use:**
1. Complete this report within 72 hours of incident resolution
2. Focus on facts, not blame - this is about learning and improvement
3. Include all relevant stakeholders in the review process
4. Be specific and actionable with improvement recommendations
5. Follow up on action items to ensure they're completed
6. Use this as an opportunity to improve systems and processes
7. Share learnings with other teams to prevent similar incidents
8. Update this template based on what you learn from each incident