# Tutorials

The intent of this document is to provide an end-user view of the capabilities of the redweed data store and API. These plain-language stories describe something in the real world that could be modelled using the media types used in the redweed data store.

## A memorable meal

One Friday night, Alice and Bob order pizzas from a nearby restaurant to eat at home. Bob has pizza fiorentina, Alice has pizza diavola. Bob makes margaritas following an old recipe. They both agree the margaritas are excellent. Alice likes her pizza but finds it slightly too spicy. Bob enjoys his pizza and gives it 5 stars. Overall, the restaurant provided good food and service.

### Entities

**People:**
- Alice (person)
- Bob (person)

**Food & Drink:**
- Pizza fiorentina (food item)
- Pizza diavola (food item)
- Margaritas (beverage)
- Recipe (instructions/document)

**Places:**
- Restaurant (business/location)
- Home (location)

**Events:**
- Travel between Home and Restaurant
- Food ordering (transaction)
- Travel between Restaurant and Home
- Friday night dinner (event/occasion)

**Attributes/Ratings:**
- 5-star rating (evaluation)
- "Too spicy" assessment (evaluation)
- "Excellent" quality rating (evaluation)

### Relationships

**Person-to-Person:**
- Alice and Bob are dining together

**Person-to-Food/Drink:**
- Bob orders pizza fiorentina
- Alice orders pizza diavola
- Bob makes margaritas
- Alice consumes pizza diavola
- Bob consumes pizza fiorentina
- Both consume margaritas

**Person-to-Place:**
- Alice and Bob are at home
- Alice and Bob order from restaurant

**Person-to-Event:**
- Alice and Bob participate in Friday night dinner
- Bob executes food ordering transaction

**Person-to-Evaluation:**
- Bob gives 5-star rating to his pizza
- Alice finds pizza too spicy
- Both rate margaritas as excellent

**Food/Drink-to-Place:**
- Pizzas sourced from restaurant
- Margaritas made at home

**Recipe-to-Beverage:**
- Recipe describes how to make margaritas

**Business-to-Transaction:**
- Restaurant fulfills food ordering transaction

## Planning a weekend trip

Sarah decides to visit her college friend Mike in Portland for the weekend. She books a flight from Seattle on Friday evening and a return flight on Sunday night. Mike recommends a hiking trail in Forest Park and suggests they try a new coffee shop downtown. Sarah finds a highly-rated hotel near Mike's apartment and makes a reservation. Mike creates a shared playlist for their road trip to the coast on Saturday. Sarah checks the weather forecast and packs accordingly.

### Entities

**People:**
- Sarah (person)
- Mike (person)

**Places:**
- Seattle (city/departure location)
- Portland (city/destination)
- Forest Park (outdoor location)
- Coffee shop (business/location)
- Hotel (accommodation)
- Mike's apartment (residence)
- Coast (geographic location)

**Transportation:**
- Friday flight (travel)
- Sunday return flight (travel)
- Road trip (travel)

**Activities:**
- Hiking trail (outdoor activity)
- Weekend visit (social event)

**Digital Content:**
- Shared playlist (media)
- Weather forecast (information)
- Hotel ratings (evaluation)

### Relationships

**Person-to-Person:**
- Sarah and Mike are college friends
- Mike provides recommendations to Sarah

**Person-to-Place:**
- Sarah travels from Seattle to Portland
- Sarah stays at hotel
- Mike lives in apartment
- Both visit Forest Park and coffee shop

**Person-to-Transportation:**
- Sarah books and takes flights
- Both participate in road trip

**Person-to-Activity:**
- Both plan to hike
- Both participate in weekend visit

**Person-to-Digital Content:**
- Mike creates playlist
- Sarah checks weather forecast
- Sarah reads hotel ratings

**Business-to-Evaluation:**
- Hotel receives ratings
- Coffee shop receives recommendation

## Learning a new skill

David wants to learn guitar and decides to take online lessons. He researches different instructors and chooses Emma, who has excellent reviews. David purchases a beginner acoustic guitar from a local music store after trying several models. Emma assigns practice exercises and tracks David's progress through a learning platform. After two months, David can play three songs and decides to join a local music group that meets weekly at the community center.

### Entities

**People:**
- David (person/student)
- Emma (person/instructor)
- Music group members (people)

**Objects:**
- Acoustic guitar (instrument)
- Multiple guitar models (instruments)

**Places:**
- Music store (business/location)
- Community center (venue)

**Services:**
- Online lessons (education service)
- Learning platform (digital service)

**Activities:**
- Practice exercises (learning activity)
- Weekly music group meetings (social activity)

**Content:**
- Instructor reviews (evaluation)
- Three songs (musical pieces)
- Progress tracking (data)

### Relationships

**Person-to-Person:**
- David learns from Emma
- David joins music group

**Person-to-Object:**
- David purchases and owns guitar
- David tries multiple guitar models

**Person-to-Service:**
- David uses online lessons
- Emma provides instruction through platform

**Person-to-Activity:**
- David practices exercises
- David attends weekly meetings
- Emma assigns exercises

**Person-to-Content:**
- David reads instructor reviews
- David learns to play songs
- Emma tracks David's progress

**Service-to-Evaluation:**
- Online lessons receive reviews

**Business-to-Transaction:**
- Music store sells guitar to David

## Managing a busy week with calendar integration

Emma is a freelance designer who uses various calendar applications for different clients. She has a Google Calendar for personal events, Outlook for her main client, and Apple Calendar for another project. She wants to consolidate her scheduling information while maintaining the ability to export updated schedules back to her tools.

On Monday, Emma imports her work calendar containing client meetings, project deadlines, and personal appointments. Tuesday morning, she adds a new project milestone with specific tasks and deliverables. Throughout the week, she logs daily journal entries about her progress and challenges. By Friday, she exports an updated calendar to share with her project manager.

### Entities

**People:**
- Emma (person/freelancer)
- Various clients (people)
- Project manager (person)

**Calendar Components:**
- Work calendar (calendar collection)
- Client meetings (events)
- Project deadlines (events with tasks)
- Personal appointments (events)
- Daily standup (recurring event)
- Project milestone (event with subtasks)

**Tasks:**
- Design mockups (task)
- Client presentation (task)
- Website review (task)
- Invoice preparation (task)

**Journal Entries:**
- Daily work log (journal)
- Challenge documentation (journal)
- Progress updates (journal)

**Digital Tools:**
- Google Calendar (external service)
- Outlook Calendar (external service)
- Apple Calendar (external service)
- Project management system (external service)

### Relationships

**Person-to-Calendar:**
- Emma owns and manages multiple calendars
- Clients have access to shared project calendars

**Calendar-to-Events:**
- Work calendar contains client meetings
- Project calendar contains milestones and deadlines
- Personal calendar contains appointments

**Events-to-Tasks:**
- Project milestone contains multiple subtasks
- Client meeting has preparation tasks
- Deadline events link to deliverable tasks

**Events-to-People:**
- Meetings have organizers and attendees
- Tasks have assignees and reviewers
- Appointments involve specific participants

**Events-to-Places:**
- Client meetings occur at office locations
- Remote meetings specify video conference platforms
- Personal appointments have location details

**Tasks-to-Journal:**
- Daily progress updates reference specific tasks
- Challenge logs relate to problematic tasks
- Completion notes document task outcomes

**Calendar-to-External-Systems:**
- Export functionality maintains sync with Google Calendar
- Import capabilities pull from Outlook
- Project management integration for task tracking

### Data Flow Examples

**Import Process:**
1. Emma uploads her work.ics file from Outlook
2. System extracts VEVENT components for meetings
3. VTODO components become project tasks
4. Recurring meetings create multiple event instances
5. Attendee information links to existing person records

**Daily Workflow:**
1. Emma logs morning journal entry about daily goals
2. Adds specific tasks for client project
3. Updates progress on existing milestone tasks
4. Links journal entries to completed tasks
5. Schedules follow-up meetings based on progress

**Export and Sharing:**
1. Emma queries events for current week
2. System generates RFC 5545 compliant iCalendar data
3. Includes only work-related events for client sharing
4. Maintains privacy for personal appointments
5. Exports updated task statuses to project management tool