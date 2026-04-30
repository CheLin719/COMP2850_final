# User Stories
**Good Food & Healthy Eating — COMP2850**

> **Version history**
> - v1 — Week 4: Initial 13 stories drafted from four personas created during needs-finding. Stories 1–5 addressed Rose's core pain points around intimidating apps and unsustainable habits. Stories 6–9 addressed Marco's pain points around recipe discovery. Stories 10–13 addressed Dr. Okafor's need to replace fragmented per-client review with a unified compliance dashboard and in-system communication. Sarah's specific needs were considered covered by the shared subscriber stories at this stage.
> - v2 — Week 6: Added 8 new stories (14–21) after frontend sprint work exposed gaps. Rose's goal to track overall energy balance led to Story 14 (Exercise Logging). Her pain point about feeling lost on first use drove Story 15 (Guided Tour) and Story 16 (Personal Settings). Sarah's family meal-planning goal had no dedicated story, leading to Story 18 (Meal Planner). Dr. Okafor's fragmented-tools pain point drove Stories 19–21 (Client Health Analytics, Appointment Management, Edit Client Meal Plan).
> - v3 — Week 8: Added 6 new stories (22–27) after a UI review identified dashboard and navigation gaps. Rose's pain point about forgetting to check separate pages led to Story 22 (Daily Nutrition Insights). Sarah's allergen and food-preference constraints led to Stories 23–24 (Allergen Filtering, Recipe Tag Filtering). Dr. Okafor's need for quick client navigation drove Story 25 (Client Search), and his goal to keep all client context in one place drove Stories 26–27 (Client Notes, Client History Timeline).
> - v4 — Week 10: Added 20 new stories (28–47) covering CSV export, guided tour, onboarding, community features, Find a Pro, appointments, fitness plans, notifications, dark mode, and accessibility. Priorities reassessed based on final implementation. All acceptance criteria verified against final codebase and marked [PASS] or [FAIL].

---

## Testing Strategy

Acceptance criteria are written persona-first: each criterion represents a real scenario that a specific persona would encounter in normal use, and PASS/FAIL status is verified against the implemented codebase.

**Rose-centred tests** focus on speed and legibility. Diary logging must complete in minimal steps (Story 1). Icon-based nutrition status must reflect real diary data (Story 4). Insight cards must appear on the main page without navigation (Story 22). Partial passes in Stories 2, 3, 4, and 22 share the same root cause: empty-state handling is inconsistent for low-frequency users.

**Marco-centred tests** focus on discovery and decision-making. Ingredient search must return ranked results for as few as one ingredient (Story 7). Recipe cards must expose cost and preparation time without a click-through (Story 6). Recipe comparison must enforce a four-recipe maximum (Story 17).

**Dr. Okafor-centred tests** focus on professional workflow efficiency. The client dashboard must load all compliance indicators without individual profile access (Story 10). Messaging must be timestamped and reject empty submissions (Story 12). Partial passes in Stories 13, 20, and 25 reflect gaps in automated scheduling and conflict detection.

**Sarah-centred tests** focus on safety and inclusivity. Allergen filters must exclude all recipes containing the selected allergen (Story 23). Dark mode must maintain readable contrast across all major pages (Story 42). WCAG 2.1 AA keyboard navigation must cover all interactive elements (Story 44).

Stories marked PARTIAL identify specific acceptance criteria that were not implemented or not consistently enforced in the final codebase.

---

## Section 1: Subscriber — Diet Monitoring & Nutritional Advice

**Story 1** — Food Diary Logging

**As a** subscriber,
**I can** log details of my food intake across six daily meal slots,
**So that** I can keep a detailed record of what I eat throughout the day and review my eating habits over time.

**Priority:** Must
**Estimate:** L
**Persona:** Rose

**Acceptance Criteria:**

**[PASS]** Given I am logged in, When I search for a food item and submit an entry with a meal slot, Then the entry is saved and appears in the correct slot for that date.

**[PASS]** Given I have logged meals across multiple days, When I open the food diary, Then I can browse past entries organised by date.

**[PASS]** Given I attempt to save an entry without selecting a food item, When I submit the form, Then the system displays a validation message and does not save the entry.

---

**Story 2** — Nutrition Trends

**As a** subscriber,
**I can** view visual summaries of my nutritional intake over time,
**So that** I can see how well I am following dietary guidelines without interpreting complex data.

**Priority:** Must
**Estimate:** M
**Persona:** Rose

**Acceptance Criteria:**

**[PASS]** Given I have logged meals for at least seven days, When I open the Trends page, Then I see a chart showing my daily intake of key nutrients for that period.

**[PASS]** Given I am viewing the weekly trends chart, When I select a specific day, Then I see a breakdown of the meals logged for that day.

**[FAIL]** Given I have fewer than two days of diary entries, When I open the Trends page, Then the system displays a message explaining that more data is needed rather than an empty chart. Empty states exist in parts of the diary UI, but the trend chart path does not consistently enforce a specific "more data needed" message.

---

**Story 3** — Nutrition Feedback

**As a** subscriber,
**I can** receive feedback on how well my diet meets nutritional guidelines,
**So that** I can understand what specific changes I should make to improve my eating habits.

**Priority:** Must
**Estimate:** M
**Persona:** Rose

**Acceptance Criteria:**

**[PASS]** Given I have logged at least three days of meals, When I open the Feedback section, Then I see at least one specific actionable recommendation based on my recent intake.

**[PASS]** Given my average sugar intake has exceeded the recommended level for five consecutive days, When I view my feedback, Then the system highlights this pattern and suggests lower-sugar alternatives.

**[FAIL]** Given I have no diary entries for the current week, When I open the Feedback section, Then the system prompts me to log meals before personalised feedback is shown. The system can still show default guidance rather than a strict "log meals first" prompt.

---

**Story 4** — Icon-Based Daily Nutrition Status

**As a** subscriber,
**I can** view my daily nutritional status through icons and colour indicators,
**So that** I can understand at a glance whether my diet is on track without interpreting numerical data.

**Priority:** Must
**Estimate:** M
**Persona:** Rose

**Acceptance Criteria:**

**[PASS]** Given I have logged meals for today, When I open the home screen, Then I see icon-based indicators for key nutrients showing green, amber, or red status based on my intake relative to recommended amounts.

**[PASS]** Given a nutrient indicator is showing red, When I select the icon, Then the system displays a plain-language explanation and a simple suggestion for improvement.

**[FAIL]** Given I have not logged any meals today, When I view the home screen, Then the nutritional status icons are shown in a neutral state rather than displaying misleading values. Not every nutrition status indicator has a dedicated neutral-state implementation.

---

**Story 5** — Personalised Dietary Suggestions

**As a** subscriber,
**I can** receive suggestions for healthier food alternatives based on my logged dietary patterns,
**So that** I can make practical improvements to my diet without researching options myself.

**Priority:** Should
**Estimate:** M
**Persona:** Rose

**Acceptance Criteria:**

**[PASS]** Given I have regularly logged high-sugar items in my diary, When I view the Suggestions page, Then I see at least one recommended lower-sugar alternative relevant to my recent entries.

**[PASS]** Given a suggestion is displayed, When I select it, Then I see a brief explanation of why this alternative is being recommended based on my diary.

**[FAIL]** Given I dismiss a suggestion, When I return to the Suggestions page on the same day, Then the dismissed suggestion does not reappear unless my diary entries change significantly. Suggestion dismissal persistence was not found in the final code.

---

**Story 14** — Exercise Logging

**As a** subscriber,
**I can** log my exercise activity alongside my food diary,
**So that** I can track my overall energy balance and not just my food intake.

**Priority:** Should
**Estimate:** M
**Persona:** Rose

**Acceptance Criteria:**

**[PASS]** Given I am on the Record Today page, When I add an exercise entry with a type and duration, Then the exercise is saved and shown in my activity log for that day.

**[PASS]** Given I have logged both food and exercise for today, When I view the dashboard, Then I can see both my calorie intake and my exercise activity in the same view.

**[PASS]** Given I submit an exercise entry without selecting an activity type, When I submit the form, Then the system displays a validation message and does not save the entry.

---

**Story 16** — Personal Settings

**As a** subscriber,
**I can** update my personal details and dietary preferences in a settings page,
**So that** the system can provide feedback and targets that are relevant to my individual profile.

**Priority:** Should
**Estimate:** M
**Persona:** Rose

**Acceptance Criteria:**

**[PASS]** Given I open the settings page, When I update my personal details such as name or dietary goals, Then the changes are saved and reflected in my profile.

**[PASS]** Given I open the preferences section, When I update my notification or display settings, Then the new preferences are applied immediately.

**[FAIL]** Given I attempt to save a required field as empty, When I submit the form, Then the system displays a validation message and does not save the changes. Some profile/backend validation exists, but not all settings fields have consistent frontend required-field validation.

---

**Story 22** — Daily Nutrition Insights

**As a** subscriber,
**I can** see automatically generated insight cards on my daily dashboard based on my logged food and exercise data,
**So that** I can receive timely, specific feedback without navigating to a separate feedback page.

**Priority:** Should
**Estimate:** M
**Persona:** Rose

**Acceptance Criteria:**

**[PASS]** Given I have logged at least one meal today, When I view the Record Today page, Then I see at least one insight card with a title and plain-language explanation relevant to my current intake.

**[PASS]** Given my intake is within recommended ranges for all nutrients, When I view my insights, Then the cards reflect a positive status rather than showing warnings.

**[FAIL]** Given I have not logged any food today, When I view the Record Today page, Then no insight cards are shown rather than displaying placeholder or default content. The insight generator can still render default cards instead of hiding all cards.

---

**Story 28** — CSV Data Export

**As a** subscriber,
**I can** export my weekly diary and exercise data as a CSV file,
**So that** I can review or share my progress outside the web app.

**Priority:** Could
**Estimate:** S
**Persona:** Rose

**Acceptance Criteria:**

**[PASS]** Given I have diary or exercise data, When I choose the export action, Then a CSV file is generated and downloaded.

**[PASS]** Given the CSV is opened, When I inspect the rows, Then it includes nutrition and exercise values such as calories, protein, carbs, fat, sugar, and exercise logs.

**[PASS]** Given I export on a new date, When the file downloads, Then the filename includes the current report date.

---

**Story 29** — Welcome Onboarding

**As a** subscriber,
**I can** complete a welcome onboarding flow with health profile questions,
**So that** my dashboard targets are personalised from the first session.

**Priority:** Should
**Estimate:** M
**Persona:** Rose

**Acceptance Criteria:**

**[PASS]** Given I am a first-time user, When the dashboard loads, Then I see a welcome onboarding modal.

**[PASS]** Given I enter profile data, When the onboarding completes, Then BMI, BMR, TDEE, calorie target, and macro guidance are calculated.

**[PASS]** Given onboarding data is saved, When I return to the dashboard, Then calorie and macro targets reflect that profile.

---

**Story 30** — Daily Target Synchronisation

**As a** subscriber,
**I can** have my personalised calorie target reused across charts, insights, and meal planning,
**So that** all parts of the dashboard give consistent guidance.

**Priority:** Should
**Estimate:** S
**Persona:** Rose

**Acceptance Criteria:**

**[PASS]** Given my daily target changes, When I return to the dashboard, Then the donut chart target updates.

**[PASS]** Given I use the meal planner, When it suggests meals, Then it uses the same daily target.

**[PASS]** Given I switch accounts, When another user logs in, Then my target does not leak into their dashboard.

---

## Section 2: Subscriber — Home Cooking & Recipes

**Story 6** — Recipe Library

**As a** subscriber,
**I can** browse a library of nutritious meal recommendations with full recipes and preparation instructions,
**So that** I can discover healthy meals I can cook at home rather than relying on ready meals.

**Priority:** Must
**Estimate:** L
**Persona:** Marco

**Acceptance Criteria:**

**[PASS]** Given I open the Recipes section, When the page loads, Then I see recipe cards each showing a title, estimated preparation time, and approximate cost.

**[PASS]** Given I select a recipe card, When the detail page opens, Then I see the full ingredient list, step-by-step instructions, and the nutritional profile of the meal.

**[FAIL]** Given the recipe data fails to load, When the page renders, Then the system displays an error message and a retry option rather than a blank page. API fallback exists in patches, but a clear retry option was not consistently found for recipe load failure.

---

**Story 7** — Ingredient-Based Recipe Search

**As a** subscriber,
**I can** search for recipes based on specific ingredients I already have,
**So that** I can find meals I can cook immediately without needing to buy additional items.

**Priority:** Must
**Estimate:** M
**Persona:** Marco

**Acceptance Criteria:**

**[PASS]** Given I enter two ingredients into the search field, When I submit the search, Then the system returns recipes that use those ingredients ranked by how many of my listed ingredients they require.

**[PASS]** Given search results are displayed, When I view each result, Then I can see how many of my entered ingredients the recipe uses without opening the full recipe.

**[PASS]** Given I submit an ingredient search with no ingredients entered, When the search runs, Then the system displays a validation message rather than returning all recipes.

---

**Story 8** — Recipe Ratings and Comments

**As a** subscriber,
**I can** rate recipes I have tried and read ratings left by other subscribers,
**So that** I can make informed decisions about which recipes to attempt based on community experience.

**Priority:** Should
**Estimate:** M
**Persona:** Marco

**Acceptance Criteria:**

**[PASS]** Given I have cooked a recipe, When I submit a star rating and an optional comment, Then my rating and comment appear on the recipe page with my username and the date.

**[PASS]** Given a recipe has received multiple ratings, When I view the recipe detail page, Then I see the average star rating and the total number of reviews.

**[PASS]** Given I attempt to submit a comment containing only whitespace, When I submit, Then the system rejects the submission and asks me to write a comment before posting.

---

**Story 9** — Recipe Favourites

**As a** subscriber,
**I can** save recipes I have tried and enjoyed to a personal favourites list,
**So that** I can quickly find and revisit meals I want to cook again without searching each time.

**Priority:** Should
**Estimate:** M
**Persona:** Marco

**Acceptance Criteria:**

**[PASS]** Given I am viewing a recipe, When I save it to my favourites, Then the recipe is added immediately and the saved state is visually indicated.

**[PASS]** Given I have saved multiple recipes, When I open my Favourites page, Then all saved recipes are displayed and I can open any of them.

**[PASS]** Given a recipe is already in my favourites, When I save it again, Then the system removes it from my favourites rather than adding a duplicate.

---

**Story 17** — Recipe Compare

**As a** subscriber,
**I can** compare the nutritional data of up to four saved recipes side by side,
**So that** I can make more informed choices about which meals best fit my dietary goals.

**Priority:** Should
**Estimate:** M
**Persona:** Marco

**Acceptance Criteria:**

**[PASS]** Given I have saved at least two recipes, When I open the Recipe Compare page and select them, Then I see a chart comparing their nutritional values side by side.

**[PASS]** Given I am viewing a comparison, When I remove a recipe, Then the chart updates immediately to reflect the remaining selections.

**[PASS]** Given I attempt to add a fifth recipe to the comparison, When I select it, Then the system prevents the addition and informs me the maximum is four recipes.

---

**Story 18** — Meal Planner

**As a** subscriber,
**I can** plan my meals for the day based on a calorie budget with recipe recommendations for each slot,
**So that** I can prepare ahead and make healthier choices rather than deciding what to eat at the last minute.

**Priority:** Should
**Estimate:** L
**Persona:** Sarah

**Acceptance Criteria:**

**[PASS]** Given I open the Meal Planner, When the page loads, Then I see recipe suggestions for each meal slot based on my daily calorie target.

**[PASS]** Given a recipe is suggested for a slot, When I lock it, Then that slot remains fixed when I refresh or swap other slots.

**[PASS]** Given I have no saved recipes, When I open the Meal Planner, Then the system displays a prompt to browse and save recipes before planning can begin.

---

**Story 23** — Allergen Filtering

**As a** subscriber,
**I can** filter recipes by excluding specific allergens such as nuts, dairy, gluten, eggs, fish, meat, and soy,
**So that** I can browse recipes that are safe and suitable for my dietary needs without checking every ingredient manually.

**Priority:** Should
**Estimate:** M
**Persona:** Sarah

**Acceptance Criteria:**

**[PASS]** Given I select one or more allergens to exclude, When the filter is applied, Then only recipes that do not contain those allergens are shown in the results.

**[PASS]** Given I have allergen filters active, When I view the recipe list, Then each visible recipe is confirmed free of my selected exclusions.

**[FAIL]** Given I select an allergen filter that excludes all available recipes, When the filter is applied, Then the system displays a message indicating no matching recipes are available rather than an empty list with no explanation. Filtering exists, but no dedicated no-results message was found for every all-excluded path.

---

**Story 24** — Recipe Tag Filtering

**As a** subscriber,
**I can** filter recipes by category tags such as Healthy, Quick, Vegan, High Protein, Comfort, and My Recipes,
**So that** I can find recipes that match my mood or goal for the day without scrolling through the full library.

**Priority:** Should
**Estimate:** S
**Persona:** Sarah

**Acceptance Criteria:**

**[PASS]** Given I select a tag filter such as Vegan, When the filter is applied, Then only recipes tagged with that category are shown.

**[PASS]** Given I select the My Recipes tag, When the filter is applied, Then only recipes I have added myself are shown.

**[FAIL]** Given no recipes match the selected tag, When the filter is applied, Then the system displays a message rather than showing an empty grid. Tag filtering exists, but a consistent tag-specific no-results message was not found.

---

**Story 31** — Custom Recipe Creation

**As a** subscriber,
**I can** create my own recipe with ingredients, steps, calories, cost, and preparation time,
**So that** I can track and reuse meals that are not already in the recipe library.

**Priority:** Could
**Estimate:** M
**Persona:** Marco

**Acceptance Criteria:**

**[PASS]** Given I open the add recipe modal, When I enter recipe details and submit, Then the new recipe appears in the recipe grid.

**[PASS]** Given I add ingredients and steps, When I view the recipe detail, Then those details are shown.

**[PASS]** Given I select My Recipes, When custom recipes exist, Then only my added recipes are shown.

---

**Story 32** — Excluded Food Settings

**As a** subscriber,
**I can** add custom excluded foods or ingredients to the recipe filters,
**So that** I can avoid foods I personally dislike or cannot eat.

**Priority:** Could
**Estimate:** S
**Persona:** Sarah

**Acceptance Criteria:**

**[PASS]** Given I type a custom excluded food, When I add it, Then it appears as a removable exclusion tag.

**[PASS]** Given exclusions are active, When recipe cards are filtered, Then recipes containing those words are hidden.

**[PASS]** Given I remove an exclusion tag, When the list updates, Then matching recipes can appear again.

---

**Story 33** — Cooking Mode

**As a** subscriber,
**I can** open a focused cooking mode for a recipe,
**So that** I can follow ingredients and preparation steps while cooking.

**Priority:** Could
**Estimate:** M
**Persona:** Marco

**Acceptance Criteria:**

**[PASS]** Given I open a recipe, When I start cooking mode, Then a focused recipe overlay appears.

**[PASS]** Given I navigate steps, When I move forward or backward, Then the current step updates.

**[PASS]** Given I exit cooking mode, When I close the overlay, Then I return to the recipe page.

---

## Section 3: Health Professional — Client Management

**Story 10** — Professional Client Dashboard

**As a** health professional,
**I can** view a dashboard showing all of my assigned clients and their recent dietary activity,
**So that** I can monitor how well my clients are following their dietary plans without opening each profile individually.

**Priority:** Must
**Estimate:** L
**Persona:** Dr. James Okafor

**Acceptance Criteria:**

**[PASS]** Given I am logged in as a health professional, When I open my dashboard, Then I see all assigned clients with each client's name, most recent diary entry date, and a colour-coded compliance indicator.

**[PASS]** Given I am viewing my dashboard, When I select a client's name, Then I am taken to that client's profile showing their food diary for the past 30 days.

**[PASS]** Given I am not authenticated as a health professional, When I attempt to access the client dashboard directly, Then I am redirected to the login page and no client data is shown.

---

**Story 11** — Client Visual Progress Indicators

**As a** health professional,
**I can** observe how well each of my clients is following their nutritional guidelines using visual progress indicators,
**So that** I can quickly identify which clients need attention without reading through individual reports.

**Priority:** Must
**Estimate:** M
**Persona:** Dr. James Okafor

**Acceptance Criteria:**

**[PASS]** Given I am on my client dashboard, When the page loads, Then each client row displays a visual status indicator based on their dietary compliance over the past seven days.

**[PASS]** Given a client has not met their nutritional guidelines for five or more consecutive days, When I view my dashboard, Then that client's status indicator shows they require follow-up.

**[PASS]** Given a client has no diary entries in the past seven days, When I view my dashboard, Then their status is shown as inactive rather than displaying a misleading compliance score.

---

**Story 12** — Professional Messaging

**As a** health professional,
**I can** send personalised dietary advice and encouragement directly to my clients through the system,
**So that** I can support my clients between appointments without contacting them by personal phone or email.

**Priority:** Must
**Estimate:** M
**Persona:** Dr. James Okafor

**Acceptance Criteria:**

**[PASS]** Given I am viewing a client's profile, When I write and send a message, Then the message is delivered to the client and a timestamp is recorded.

**[FAIL]** Given I have sent a message to a client, When the client reads the message, Then a read receipt is shown on my sent messages view. Messaging is implemented, but explicit read receipts were not found.

**[PASS]** Given I attempt to send a message with an empty text field, When I submit, Then the system prevents the submission and asks me to write a message first.

---

**Story 13** — Automatic Inactivity Alerts

**As a** health professional,
**I can** receive automatic alerts when a client has not logged their food diary for three or more consecutive days,
**So that** I can intervene early rather than discovering inactivity only at their next appointment.

**Priority:** Should
**Estimate:** M
**Persona:** Dr. James Okafor

**Acceptance Criteria:**

**[PASS]** Given a client assigned to me has not logged any entries for three consecutive days, When the system's daily check runs, Then an alert flag appears next to that client's name on my dashboard.

**[PASS]** Given an alert is active for a client, When I view the alert details, Then I see the client's name, the number of days without an entry, and the date of their last entry.

**[FAIL]** Given I have dismissed an alert for a client, When that client stops logging again for three days after a new entry, Then a new alert is triggered rather than remaining in the dismissed state. Alert-style UI exists, but no full scheduled dismiss/retrigger workflow was found.

---

**Story 19** — Client Health Analytics

**As a** health professional,
**I can** view detailed analytics for each client including BMI, body metrics, and health scores across nutrition, consistency, hydration, and exercise,
**So that** I can assess a client's overall wellbeing at a glance rather than reading through raw diary entries.

**Priority:** Should
**Estimate:** L
**Persona:** Dr. James Okafor

**Acceptance Criteria:**

**[PASS]** Given I open a client's profile, When I view the analytics tab, Then I see their BMI, weight, and a breakdown of health scores across at least four categories.

**[PASS]** Given a client's score in any category is critically low, When I view their analytics, Then that category is visually highlighted to indicate it requires attention.

**[FAIL]** Given a client has no diary entries, When I view their analytics, Then the system displays a message indicating insufficient data rather than showing empty or zero scores. The analytics tab does not consistently show a dedicated insufficient-data state.

---

**Story 20** — Appointment Management

**As a** health professional,
**I can** view and manage scheduled appointments with my clients within the system,
**So that** I can keep track of upcoming consultations without relying on a separate calendar tool.

**Priority:** Should
**Estimate:** M
**Persona:** Dr. James Okafor

**Acceptance Criteria:**

**[PASS]** Given I open a client's profile, When I select the Appointments tab, Then I see a list of upcoming appointments with the date, time, and type of session.

**[PASS]** Given I have multiple clients with appointments on the same day, When I view the overview dashboard, Then I can see all of that day's appointments in one place.

**[FAIL]** Given I attempt to schedule an appointment at a time that conflicts with an existing one, When I confirm the booking, Then the system alerts me to the conflict rather than creating a duplicate. Appointment creation exists, but conflict detection is not implemented in the backend controller.

---

**Story 21** — Edit Client Meal Plan

**As a** health professional,
**I can** create and edit a weekly meal plan for each of my clients directly within their profile,
**So that** I can provide structured dietary guidance that goes beyond text-based advice messages.

**Priority:** Should
**Estimate:** M
**Persona:** Dr. James Okafor

**Acceptance Criteria:**

**[PASS]** Given I open a client's profile, When I select the Plan tab, Then I see their current weekly meal plan with one entry per day.

**[PASS]** Given I edit a day's entry in the plan, When I save the changes, Then the updated plan is immediately visible on the client's profile.

**[FAIL]** Given I attempt to save a plan with a day left completely empty, When I submit, Then the system asks me to confirm whether I intend to leave that day without a plan entry. Plan save exists, but explicit confirmation for empty days was not found.

---

**Story 25** — Client Search

**As a** health professional,
**I can** search for a specific client by name using a search input in the client sidebar,
**So that** I can navigate directly to a client's profile without scrolling through the full client list.

**Priority:** Should
**Estimate:** S
**Persona:** Dr. James Okafor

**Acceptance Criteria:**

**[PASS]** Given I have multiple clients listed in the sidebar, When I type a client's name into the search field, Then the list filters in real time to show only matching clients.

**[PASS]** Given I clear the search field, When the input is empty, Then the full client list is restored.

**[FAIL]** Given I search for a name that does not match any client, When the search runs, Then the system displays a message indicating no matching clients were found rather than showing an empty list. Search filtering exists, but a dedicated no-match message was not consistently found.

---

**Story 26** — Client Notes

**As a** health professional,
**I can** write and view private notes about each client within their profile,
**So that** I can record observations, concerns, and recommendations that inform my ongoing care decisions without relying on external documents.

**Priority:** Should
**Estimate:** S
**Persona:** Dr. James Okafor

**Acceptance Criteria:**

**[PASS]** Given I open a client's profile, When I view the notes section, Then I see the current notes for that client including any previously saved observations.

**[PASS]** Given I update a client's notes, When I save the changes, Then the updated notes are immediately visible when I next open that client's profile.

**[FAIL]** Given I attempt to save a note that exceeds the maximum character limit, When I submit, Then the system prevents the save and indicates how many characters are allowed. Pro notes are editable, but no maximum character validation was found.

---

**Story 27** — Client History Timeline

**As a** health professional,
**I can** view a chronological history of key events for each client including check-ins, plan updates, and initial assessments,
**So that** I can understand a client's progress over time without relying on memory or separate records.

**Priority:** Could
**Estimate:** M
**Persona:** Dr. James Okafor

**Acceptance Criteria:**

**[PASS]** Given I open a client's profile, When I view the history section, Then I see a timeline of past events listed in reverse chronological order with dates and event types.

**[PASS]** Given a new check-in or plan update occurs, When I view the client's history, Then the new event appears at the top of the timeline.

**[PASS]** Given a client has no recorded history, When I view their history section, Then the system displays a message indicating no events have been recorded rather than an empty list.

---

**Story 34** — Expert Fitness Plan

**As a** health professional,
**I can** create and edit a fitness plan for each client,
**So that** exercise advice is recorded alongside nutrition guidance.

**Priority:** Should
**Estimate:** M
**Persona:** Dr. James Okafor

**Acceptance Criteria:**

**[PASS]** Given I open a client's profile, When I select the Fitness Plan tab, Then training plan rows are displayed.

**[PASS]** Given I edit the fitness plan, When I save, Then the plan is reflected immediately in the client profile.

**[PASS]** Given a client has no plan, When I view the plan tab, Then a clear empty state is shown.

---

**Story 35** — Pro Notes

**As a** health professional,
**I can** maintain private pro notes for each client,
**So that** I can preserve context for future consultations.

**Priority:** Should
**Estimate:** S
**Persona:** Dr. James Okafor

**Acceptance Criteria:**

**[PASS]** Given I view a client plan area, When pro notes exist, Then they are displayed separately from client-facing messages.

**[PASS]** Given I click edit, When I update the note text, Then the visible note changes.

**[PASS]** Given the subscriber views their My Nutritionist plan, When pro notes are available, Then they can see the relevant note content.

---

**Story 36** — Professional Settings

**As a** health professional,
**I can** update my professional profile and preferences,
**So that** my account settings match my working style.

**Priority:** Could
**Estimate:** M
**Persona:** Dr. James Okafor

**Acceptance Criteria:**

**[PASS]** Given I open the professional avatar menu, When I choose settings, Then a settings overlay opens.

**[PASS]** Given I switch settings panels, When I select personal or preference sections, Then the relevant controls are shown.

**[PASS]** Given I close the settings overlay, When I return to the dashboard, Then navigation and sidebar layout remains intact.

---

## Section 4: Community & Social Features

**Story 37** — Community Posts

**As a** subscriber,
**I can** create and read community posts,
**So that** I can share progress, questions, and experiences with other users.

**Priority:** Could
**Estimate:** M
**Persona:** Marco

**Acceptance Criteria:**

**[PASS]** Given I open the community hub, When posts exist, Then I can read a feed of community posts.

**[PASS]** Given I write a post, When I submit it, Then it appears in the feed.

**[PASS]** Given there are no posts, When I open the hub, Then an empty community state is shown.

---

**Story 38** — Likes and Replies

**As a** subscriber,
**I can** like posts and reply to community discussions,
**So that** I can participate socially rather than only reading content.

**Priority:** Could
**Estimate:** M
**Persona:** Marco

**Acceptance Criteria:**

**[PASS]** Given I click like on a post, When the action is applied, Then the like state and count updates.

**[PASS]** Given I write a reply, When I submit it, Then the reply appears under the post.

**[PASS]** Given a professional replies, When I view the reply, Then it is visually marked as a pro response.

---

**Story 39** — Find a Pro and Bind Requests

**As a** subscriber,
**I can** browse available professionals and send a bind request,
**So that** I can receive personalised support from a nutrition expert.

**Priority:** Should
**Estimate:** L
**Persona:** Rose

**Acceptance Criteria:**

**[PASS]** Given I have no nutritionist, When I open My Nutritionist or community expert matching, Then I can browse available professionals.

**[PASS]** Given I choose a professional, When I send a request, Then the request appears for that professional.

**[PASS]** Given the professional accepts, When I return to My Nutritionist, Then I can see the connected professional relationship.

---

**Story 40** — Professional Community Tools

**As a** health professional,
**I can** view community questions, client posts, bind requests, and my own posts,
**So that** I can support users and manage community engagement efficiently.

**Priority:** Could
**Estimate:** M
**Persona:** Dr. James Okafor

**Acceptance Criteria:**

**[PASS]** Given I open the professional community page, When I switch tabs, Then I can view feed, unanswered questions, clients, bind requests, and my posts.

**[PASS]** Given I post as a professional, When the post appears, Then it is marked as a professional contribution.

**[PASS]** Given there are no bind requests or unanswered posts, When I open those tabs, Then clear empty states are shown.

---

**Story 41** — Dev Log

**As a** health professional,
**I can** view a development log inside the professional community area,
**So that** I can understand recent platform updates and feature changes that affect my professional workflow.

**Priority:** Could
**Estimate:** S
**Persona:** Dr. James Okafor

**Acceptance Criteria:**

**[PASS]** Given I open the professional community tabs, When I select Dev Log, Then dated feature and improvement entries are displayed.

**[PASS]** Given I read each entry, When I scan the title and body, Then I can understand what was delivered.

**[PASS]** Given the page is in dark or light mode, When Dev Log cards render, Then they remain readable.

---

## Section 5: System & UX Features

**Story 15** — Guided Tour

**As a** subscriber,
**I can** follow a step-by-step guided tour of the system when I first log in,
**So that** I can understand how to use the key features without needing to ask for help.

**Priority:** Should
**Estimate:** M
**Persona:** Rose

**Acceptance Criteria:**

**[PASS]** Given I log in for the first time, When the dashboard loads, Then an onboarding guide launches and walks me through the main features in clearly labelled steps.

**[PASS]** Given I am at any step of the guide, When I choose to skip, Then the guide closes and I am taken directly to the dashboard.

**[PASS]** Given I skipped the guide on first login, When I later want to revisit it, Then I can relaunch it from the dashboard at any time.

---

**Story 42** — Dark Mode

**As a** user,
**I can** switch the interface into dark mode,
**So that** I can use the system comfortably in low-light environments.

**Priority:** Could
**Estimate:** S
**Persona:** Sarah

**Acceptance Criteria:**

**[PASS]** Given I click the dark mode toggle, When the mode changes, Then the page switches between light and dark visual styles.

**[PASS]** Given dark mode is active, When I navigate major pages, Then text and controls remain readable.

**[PASS]** Given I refresh or continue using the app, When dark mode is stored, Then the preferred style remains available.

---

**Story 43** — Real-Time Notifications

**As a** subscriber or professional,
**I can** receive notifications when messages, plan updates, or relationship changes occur,
**So that** I do not miss important updates from the other side of the care relationship.

**Priority:** Should
**Estimate:** L
**Persona:** Rose / Dr. James Okafor

**Acceptance Criteria:**

**[PASS]** Given a professional sends a message, When the backend saves it, Then a notification record is created for the recipient.

**[PASS]** Given a plan is updated or deleted, When the event is published, Then the subscriber receives a notification event.

**[PASS]** Given the user is connected through WebSocket or polling, When unread notifications exist, Then the UI can display unread count and mark notifications read.

---

**Story 44** — WCAG 2.1 AA Accessibility Support

**As a** user with accessibility needs,
**I can** navigate the application using semantic labels, keyboard interactions, and readable contrast,
**So that** I can use NourishWell without relying only on mouse or visual cues.

**Priority:** Must
**Estimate:** L
**Persona:** Sarah

**Acceptance Criteria:**

**[PASS]** Given I use keyboard navigation, When I focus interactive elements, Then controls have visible focus and Enter/Space behaviour where appropriate.

**[PASS]** Given I use assistive technology, When I reach dialogs, buttons, search fields, tabs, and charts, Then ARIA labels and roles provide context.

**[PASS]** Given I prefer reduced motion or print-readable output, When the app renders, Then accessibility-oriented CSS and print styles support readability.

---

**Story 45** — Keyboard Shortcuts

**As a** returning subscriber,
**I can** use keyboard shortcuts to navigate common dashboard areas,
**So that** repeated use of the system is faster.

**Priority:** Could
**Estimate:** S
**Persona:** Rose

**Acceptance Criteria:**

**[PASS]** Given I press the keyboard help key, When shortcuts are available, Then a shortcuts dialog opens.

**[PASS]** Given I press a supported shortcut, When I am on the dashboard, Then the relevant page opens.

**[PASS]** Given I close the shortcuts dialog, When I continue navigating, Then focus and page state remain usable.

---

**Story 46** — Secure Authentication and Role Guards

**As a** system user,
**I can** access only the pages and APIs allowed for my role,
**So that** subscriber and professional data remains separated.

**Priority:** Must
**Estimate:** L
**Persona:** Rose / Dr. James Okafor

**Acceptance Criteria:**

**[PASS]** Given I register or log in, When credentials are valid, Then I receive a JWT-backed session.

**[PASS]** Given I access protected API routes, When I do not provide a valid token, Then access is rejected.

**[PASS]** Given I am a subscriber, When I access professional-only client APIs, Then the request is rejected.

---

**Story 47** — Frontend-Backend Integration Layer

**As a** system user,
**I can** use connected frontend pages that reliably communicate with the backend,
**So that** my diary, recipe, notification, and client-management actions are saved and protected consistently.

**Priority:** Should
**Estimate:** M
**Persona:** Rose / Dr. James Okafor

**Acceptance Criteria:**

**[PASS]** Given the frontend calls protected endpoints, When a token exists, Then it is attached as a Bearer token.

**[PASS]** Given the app runs locally, When requests are made, Then relative API paths avoid hardcoded deployment and CORS issues.

**[PASS]** Given API calls fail, When the response is handled, Then the helper reports or throws errors consistently enough for UI flows to respond.

