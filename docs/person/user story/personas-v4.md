# Personas
**Good Food & Healthy Eating — COMP2850**

> **Version history**
> - v1 — Week 4: Initial four personas drafted based on project brief and needs-finding
> - v2 — Week 6: Goals updated to reflect new features identified during frontend development (exercise logging, onboarding, meal planner, recipe comparison, health analytics, appointment management, meal plan editing)
> - v3 — Week 8: Goals updated for Rose, Marco, Sarah, and Dr. Okafor to reflect latest features: daily insights, allergen filtering, recipe tag filtering, client search, client notes, and client history timeline
> - v4 — Week 10: Final version — Rose updated with CSV export, guided tour, and daily nutrition insights goals. Dr. Okafor updated with real-time client data sync, WebSocket notifications, client search, and appointment management goals. Marco updated with custom recipe creation, community rating, and allergen filtering goals. Sarah updated with exercise tracking, meal planner, and daily nutrition targets goals. Success criteria verified against final codebase.

---

## Rose
**Role:** Subscriber — Executive Assistant at a media company
**Environment:** Relies entirely on digital channels — apps, websites, and social media — for food decisions. Does not engage with printed materials.
**Demographic:** 31 years old, lives alone, long working hours, high-pressure environment, frequent takeaway user.
**Trigger:** Frequently orders takeaway and consumes high-sugar snacks to cope with stress. Wants to feel more in control of her eating habits without learning complicated diet rules.

### Pain Points
- After stressful workdays she drinks milk tea or eats desserts as a coping mechanism. Currently: promises herself to reduce sugar the next day but the cycle repeats.
- When concerned about her weight she tries to drastically cut food intake. Currently: these extreme attempts are unsustainable and she returns to previous habits within days.
- Living alone makes takeaway the easiest option after a long day. Currently: chooses food based on speed and convenience rather than nutritional value.
- Most health apps present complex data and charts that feel intimidating. Currently: ignores most of the information and only pays attention to the simplest indicators.
- When app features are spread across several pages, she forgets to check them unless the dashboard gives clear prompts.

### Goals
- Implement a personalised suggestion feature that recommends healthier alternatives to high-sugar drinks and snacks.
- Implement a food diary that allows users to log meals across six daily slots and track eating patterns over time.
- Provide a simplified nutrition overview that highlights key indicators without overwhelming detail.
- Implement a gradual habit-building tool that sets small, achievable dietary goals rather than extreme restrictions.
- Implement an exercise logging feature so users can track activity alongside food intake and see their overall energy balance.
- Provide a step-by-step onboarding guide so first-time users can learn the system without needing to ask for help.
- Display automatically generated daily insight cards on the dashboard so users receive timely feedback without navigating to a separate page.
- Allow users to export weekly diary and exercise data as a CSV report.
- Allow users to find and connect with a nutrition professional when they need extra support.
- Provide notifications for important updates such as messages, plan changes, and professional relationship changes.

### Job Stories
- When I come home exhausted after a long workday, I want to log what I ate in under two minutes, so I can maintain a consistent diary without it feeling like another burden.
- When I have eaten a sugary snack as a stress response, I want to see a healthier alternative suggested automatically, so I can gradually replace the habit without researching it myself.
- When I open the app each morning, I want to see my previous day's nutrition as clear icons rather than numbers, so I can decide how to adjust today's meals without interpreting charts.
- When I sign up for the first time, I want a guided setup that personalises my calorie and macro targets from my health profile, so I can start receiving relevant feedback from the very first session.
- When I feel I need more structured support, I want to find and connect with a nutrition professional directly in the platform, so I can access expert guidance without a separate web search.

### Success Criteria
- She receives at least one healthier snack or drink suggestion per day relevant to her logged food choices.
- She can log a full day of meals in under two minutes without needing to read instructions.
- She can view a simple weekly summary of her eating habits on a single screen.
- She completes onboarding once and sees calorie and macronutrient targets reflected in the dashboard.
- She can export her weekly progress and connect with a professional without leaving the main system.

### Constraints
- Very limited time and energy after work — any interaction must be completable in under three minutes.
- High stress levels mean she is likely to abandon features that feel effortful or judgmental.
- Lives alone with no external support for cooking or diet planning.
- Relies entirely on digital channels for food information and services.

### Non-Goals
- Does not need advanced medical-grade nutritional analysis or calorie calculations.
- Does not want to manage other users' accounts or view health professional reports.
- Not interested in complex recipe instructions at this stage.

### Related User Stories
- Story 1 — Food Diary Logging
- Story 2 — Nutrition Trends
- Story 3 — Nutrition Feedback
- Story 4 — Icon-Based Daily Nutrition Status
- Story 5 — Personalised Dietary Suggestions
- Story 14 — Exercise Logging
- Story 15 — Guided Tour
- Story 16 — Personal Settings
- Story 22 — Daily Nutrition Insights
- Story 28 — CSV Data Export
- Story 29 — Welcome Onboarding
- Story 30 — Daily Target Synchronisation
- Story 39 — Find a Pro and Bind Requests
- Story 43 — Real-Time Notifications
- Story 45 — Keyboard Shortcuts
- Story 46 — Secure Authentication and Role Guards

---

## Dr. James Okafor
**Role:** Health Professional — Registered Nutritionist
**Environment:** Uses a desktop computer at the clinic during consultations and a tablet when reviewing client progress remotely.
**Demographic:** 47 years old, runs a private practice in Leeds, manages over 30 clients across different age groups, works full-time with back-to-back appointments.
**Trigger:** His clinic has joined the platform to support remote dietary monitoring. Needs to oversee all clients in one place and send timely advice without scheduling a call for every update.

### Pain Points
- Client food diary entries are scattered with no aggregated overview, so he must open each profile individually. Currently: manually reviews each client one by one, which means some clients are inadvertently overlooked between appointments.
- Every piece of advice must be delivered individually with no in-system communication tool. Currently: contacts clients via personal email or phone, with no record kept inside the platform.
- There is no mechanism to notify him when a client misses several days of logging. Currently: only discovers problems at the next scheduled appointment, by which point habits may have worsened considerably.
- Appointment details, meal plans, notes, and exercise advice can become fragmented across different tools.
- Community questions and professional-client connection requests are difficult to manage if they are separate from the main dashboard.

### Goals
- Provide a client management dashboard displaying all assigned clients' recent diary activity and compliance status in a single view.
- Implement an in-system messaging feature so health professionals can send personalised advice directly to clients.
- Implement automatic alerts that notify the professional when a client has not logged food for three or more consecutive days.
- Provide visual progress indicators for each client so the professional can quickly assess who needs attention without opening individual profiles.
- Provide detailed per-client health analytics including BMI, body metrics, and health scores across nutrition, consistency, hydration, and exercise.
- Implement appointment management so the professional can view and schedule client consultations within the system.
- Allow the professional to create and edit a weekly meal plan for each client directly within their profile.
- Implement a client search feature so the professional can navigate directly to a specific client without scrolling through the full list.
- Provide a private notes field per client so the professional can record observations and recommendations within the system.
- Display a chronological history timeline per client showing past check-ins, plan updates, and assessments.
- Allow the professional to create and edit client fitness plans alongside nutrition plans.
- Provide professional community tools for posts, unanswered questions, bind requests, and development log visibility.
- Provide real-time synchronisation of client diary and profile data so the professional dashboard always reflects the latest subscriber activity.
- Provide WebSocket-based notifications so the professional receives immediate in-app alerts when client-facing events occur.

### Job Stories
- When I sit down between back-to-back appointments, I want to scan all my clients' diary compliance in a single view, so I can identify who needs attention without opening each profile individually.
- When a client has been inactive for several days, I want to receive an automatic alert, so I can reach out early rather than discovering the gap only at the next scheduled appointment.
- When I want to send follow-up advice after a consultation, I want to message a client directly through the platform, so all communication is recorded in one place.
- When I am reviewing a client's progress, I want to see their full history, clinical notes, meal plan, fitness plan, and health analytics from a single profile area.
- When a new client requests to connect with me, I want to manage that bind request from my professional dashboard, so I can accept or decline without interrupting my existing workflow.

### Success Criteria
- He can view all clients' most recent diary date and a compliance indicator on a single page without opening individual profiles.
- He can compose and send a message to a client in under one minute from the client's profile.
- The system automatically flags a client when they have not logged food for three consecutive days.
- He can view a client's BMI, health scores, appointments, notes, plans, and timeline from a single profile area.
- He can manage bind requests and professional community activity from the professional interface.

### Constraints
- Subject to GDPR — all client data must be accessible only to the assigned professional.
- Back-to-back appointments mean each client review must be completable in under ten minutes.
- Must work reliably on both desktop and tablet without additional software installation.

### Non-Goals
- Does not need to log his own personal food diary.
- Does not need to browse, rate, or save recipes.
- Does not need to directly edit a client's food diary entries.

### Related User Stories
- Story 10 — Professional Client Dashboard
- Story 11 — Client Visual Progress Indicators
- Story 12 — Professional Messaging
- Story 13 — Automatic Inactivity Alerts
- Story 19 — Client Health Analytics
- Story 20 — Appointment Management
- Story 21 — Edit Client Meal Plan
- Story 25 — Client Search
- Story 26 — Client Notes
- Story 27 — Client History Timeline
- Story 34 — Expert Fitness Plan
- Story 35 — Pro Notes
- Story 36 — Professional Settings
- Story 40 — Professional Community Tools
- Story 41 — Dev Log
- Story 43 — Real-Time Notifications
- Story 46 — Secure Authentication and Role Guards

---

## Marco Ferrari
**Role:** Subscriber — Postgraduate Student (Home Cook focus)
**Environment:** Uses a tablet in the kitchen while cooking and his phone at the supermarket to check ingredient lists and costs.
**Demographic:** 27 years old, originally from Italy, postgraduate student at the University of Leeds, lives in a shared house, very tight weekly budget, beginner-level cooking skills.
**Trigger:** Has realised that ready meals are both expensive and unhealthy. Wants to start cooking at home but does not know which recipes are affordable, quick, and achievable for a beginner.

### Pain Points
- Has several ingredients in his fridge but cannot search for recipes using only those items. Currently: searches the web randomly, finds recipes requiring ingredients he does not have, and ends up buying a ready meal instead.
- Cannot tell from the recipe listing whether a dish fits his budget or time constraints until he has opened the full page. Currently: wastes time opening and discarding multiple recipes before finding one that might work.
- Recipe descriptions give no indication of actual difficulty level. Currently: attempts recipes at random, sometimes fails, wastes ingredients and money, and gradually loses motivation to try again.
- When he finds and cooks a recipe he likes, there is no feature to save it. Currently: takes a screenshot or tries to remember where he found it, and frequently cannot locate it again.
- He wants to learn from other users' experiences before trying unfamiliar meals.

### Goals
- Provide an ingredient-based recipe search that returns results ranked by how many of the user's listed ingredients they require.
- Provide recipe cards that clearly show estimated preparation time and approximate cost before the user opens the full recipe.
- Implement a community rating and comment system so users can read honest feedback on difficulty before choosing a recipe.
- Implement a favourites feature so users can save recipes they have tried and enjoyed.
- Provide a recipe comparison tool so users can compare the nutritional and cost data of up to four recipes side by side before deciding which to cook.
- Implement recipe creation so users can save meals that are not already in the recipe library.
- Provide a focused cooking mode so users can follow ingredients and steps while cooking.
- Support community posts, likes, and replies so users can share questions and progress.
- Allow users to filter recipes by allergen exclusions and category tags so Marco can narrow the library to dishes that match his dietary requirements and cooking goals.

### Job Stories
- When I am standing in front of my fridge not knowing what to cook, I want to search for recipes by the ingredients I already have, so I can find a meal I can make right now without an unplanned shopping trip.
- When I am browsing the recipe library, I want to see preparation time and estimated cost on each card before I open a recipe, so I can dismiss unsuitable options instantly.
- When I cook a recipe I enjoy, I want to save it with a single action, so I can find it again next week without searching from scratch.
- When I am deciding between two or three recipe options, I want to compare their nutritional and cost data side by side, so I can pick the one that best fits my budget and health goals.
- When I am actively cooking, I want a focused step-by-step mode that shows only the current step, so I can follow along without accidentally scrolling to the wrong part of the screen.

### Success Criteria
- He can enter three or fewer ingredients and receive a ranked list of matching recipes within 30 seconds.
- Estimated preparation time and approximate cost are visible on each recipe card without opening the full recipe.
- He can save a recipe to his favourites with a single action and retrieve it from his favourites page at any time.
- He can compare up to four recipes side by side to identify which best fits his budget and nutritional needs.
- He can create his own recipe and participate in community discussions.

### Constraints
- Weekly grocery budget of no more than £30.
- Meals must be preparable in under 30 minutes with basic kitchen equipment.
- Beginner cooking level — recipes must use plain language with no assumed technical knowledge.

### Non-Goals
- Does not need detailed nutritional analysis or diet tracking features.
- Does not need to interact with a health professional or access professional dietary advice.

### Related User Stories
- Story 6 — Recipe Library
- Story 7 — Ingredient-Based Recipe Search
- Story 8 — Recipe Ratings and Comments
- Story 9 — Recipe Favourites
- Story 17 — Recipe Compare
- Story 31 — Custom Recipe Creation
- Story 33 — Cooking Mode
- Story 37 — Community Posts
- Story 38 — Likes and Replies

---

## Sarah Thompson
**Role:** Subscriber — Parent Concerned About Family Nutrition
**Environment:** Uses her phone throughout the day — in the kitchen while cooking, at the supermarket, and in the evening when planning meals for the week.
**Demographic:** 38 years old, full-time office administrator, married with two children aged 7 and 10, responsible for most of the family's cooking and food shopping.
**Trigger:** After reading about increasing childhood obesity rates in the UK, wants to improve her family's diet but does not know where to start. Currently cooks mostly from habit with no clear sense of whether meals meet nutritional guidelines.

### Pain Points
- Has no tool to assess whether the meals she cooks contain the right balance of nutrients. Currently: relies on rough guesswork with no clear feedback on whether her family's diet is actually healthy.
- Most recipe platforms are not tailored to families with children — recipes are too complex, too expensive, or not nutritionally focused. Currently: falls back on the same small rotation of meals her family already accepts.
- Nutrition labels and charts use numbers and terminology that require effort to understand. Currently: ignores detailed nutritional breakdowns and relies on vague impressions of whether a meal is healthy enough.
- When her children enjoy a meal, she has no efficient way to save and retrieve that recipe. Currently: bookmarks pages in a browser, resulting in a disorganised collection she rarely revisits.
- Food allergies, disliked ingredients, and low-light evening planning make recipe selection harder.

### Goals
- Implement a food diary and nutritional tracking feature that provides clear visual feedback on whether daily meals meet recommended guidelines.
- Provide icon-based nutritional summaries that communicate dietary information visually without requiring users to interpret numbers.
- Provide a searchable recipe library that includes family-friendly nutritious meals with clear preparation times and cost estimates.
- Implement a favourites feature so users can save approved family recipes and retrieve them easily.
- Implement a meal planner so users can organise meals for the day based on a calorie budget, helping with advance family meal preparation.
- Implement allergen filtering so parents can exclude recipes containing ingredients their children cannot eat.
- Implement custom excluded food settings so users can hide recipes containing disliked or unsuitable ingredients.
- Provide dark mode and accessibility support so the app remains readable and usable in different environments.

### Job Stories
- When I cook a meal for the family, I want to log it and immediately see a clear visual indicator of whether it meets that day's nutritional guidelines, so I can feel confident about my family's diet without interpreting numbers.
- When I am planning the week's meals on a Sunday evening, I want to use the meal planner to organise meals within a calorie budget, so I can prepare ahead rather than deciding what to cook at the last minute each day.
- When I am searching for recipes, I want to filter out allergens and disliked ingredients in a single step, so I can browse the full library without manually checking every ingredient list.
- When I open the app while tired after putting the children to bed, I want an icon-based dashboard I can understand at a glance, so I can check the day's nutrition without interpreting charts or numbers in low-light conditions.

### Success Criteria
- She can log a family meal and immediately see a clear visual indicator of whether it meets the recommended nutritional guidelines for that day.
- She can find at least three family-friendly healthy recipes within two minutes using the search feature.
- She can save a recipe to favourites in one action and access her full saved list from a dedicated page.
- She can use the meal planner to plan a full day of family meals within her calorie budget without navigating multiple pages.
- She can filter out allergens and excluded foods before choosing recipes.

### Constraints
- Busy daily schedule with children — interactions must be quick and require no prior reading or training.
- Needs an interface that relies on icons and visuals rather than text, accessible during rushed moments.
- Family budget constraints mean recipes must be practically affordable for a family of four.
- Needs readable contrast, keyboard support, and clear labels.

### Non-Goals
- Does not need to interact with a health professional or receive clinical dietary advice.
- Does not need advanced sports nutrition or fitness-specific tracking features.
- Does not need to manage accounts for other users or view professional dashboards.

### Related User Stories
- Story 18 — Meal Planner
- Story 23 — Allergen Filtering
- Story 24 — Recipe Tag Filtering
- Story 32 — Excluded Food Settings
- Story 42 — Dark Mode
- Story 44 — WCAG 2.1 AA Accessibility Support

---

## Persona → System Design Impact

| Persona | Pain Point | Design Decision | Stories |
|---|---|---|---|
| Rose | Complex health apps feel intimidating | Icon-based nutrition status (green/amber/red) instead of raw numbers | Story 4, Story 22 |
| Rose | Forgets to check features unless dashboard prompts her | Daily insight cards generated automatically on Record Today page | Story 22 |
| Rose | Extreme dietary restrictions are unsustainable | Personalised suggestions tied to actual logged patterns | Story 5 |
| Rose | Lives alone with no external dietary support | Find a Pro feature and bind request flow | Story 39 |
| Rose | Loses track of feature access across multiple pages | Unified dashboard with onboarding, calorie target sync, and keyboard shortcuts | Story 15, Story 29, Story 30, Story 45 |
| Marco | Cannot search recipes by available ingredients | Ingredient-based recipe search ranked by match count | Story 7 |
| Marco | Cannot assess cost or time suitability without opening the full recipe | Preparation time and cost visible on every recipe card | Story 6, Story 17 |
| Marco | Loses recipes he has cooked and enjoyed | Favourites feature with single-action save/unsave toggle | Story 9, Story 31 |
| Marco | Wants to learn from other users before attempting a recipe | Community ratings, comments, and reply system | Story 8, Story 37, Story 38 |
| Dr. Okafor | Must open each client profile individually | Client dashboard with colour-coded compliance indicators per row | Story 10, Story 11 |
| Dr. Okafor | Contacts clients via personal phone or email | In-system professional messaging with timestamp records | Story 12, Story 43 |
| Dr. Okafor | Discovers client inactivity only at next appointment | Automatic three-day inactivity alert on the dashboard | Story 13 |
| Dr. Okafor | Client notes, plans, and analytics are fragmented | Unified client profile combining notes, meal plan, fitness plan, analytics, and history | Story 19, Story 21, Story 26, Story 27, Story 34, Story 35 |
| Sarah | No tool to assess whether family meals meet nutritional guidelines | Food diary with immediate nutritional feedback after each logged meal | Story 1, Story 3 |
| Sarah | Nutrition labels require effort to interpret | Icon-based daily nutrition status with plain-language explanations | Story 4 |
| Sarah | Children have allergens and food dislikes | Allergen filtering and custom excluded food settings | Story 23, Story 32 |
| Sarah | Uses the app in rushed or low-light moments | Dark mode toggle and WCAG 2.1 AA accessibility support | Story 42, Story 44 |
