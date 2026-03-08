# User Stories
**Good Food & Healthy Eating — COMP2850**

---

## Section 1 — Subscriber: Diet Monitoring & Nutritional Advice

### Story 1 — Food Diary

**As a** subscriber to the service,  
**I can** log details of my food intake in a daily food diary,  
**So that** I can keep a record of what I eat and review my eating habits over time.

**Acceptance Criteria:**

| Status | Given | When | Then |
|--------|-------|------|------|
| ✅ PASS | I am logged in | I search for a food item and enter a portion size and meal type | The entry is saved and appears in my diary for that date |
| ✅ PASS | I have logged meals across multiple days | I open the food diary | I can browse past entries by date in chronological order |
| ❌ FAIL | I attempt to save a diary entry without selecting a food item | I tap Save | The system displays a validation message asking me to select a food before saving |

---

### Story 2 — Nutritional Trends

**As a** subscriber,  
**I can** view simple and informative visual summaries of my nutritional intake over time,  
**So that** I can see how well I am following dietary guidelines without needing to interpret complex data.

**Acceptance Criteria:**

| Status | Given | When | Then |
|--------|-------|------|------|
| ✅ PASS | I have logged meals for at least seven days | I open the Trends page | I see a chart showing my daily intake of key nutrients including calories, sugar, and protein for that period |
| ✅ PASS | I am viewing the weekly trends chart | I tap on a specific day's bar | I see a breakdown of the meals logged for that day |
| ❌ FAIL | I have fewer than two days of diary entries | I open the Trends page | The system displays a message explaining that more data is needed rather than showing an empty or broken chart |

---

### Story 3 — Nutritional Guidelines Feedback

**As a** subscriber,  
**I can** receive clear feedback from the system on how well my diet meets nutritional guidelines,  
**So that** I can understand what specific changes I should make to improve my eating habits.

**Acceptance Criteria:**

| Status | Given | When | Then |
|--------|-------|------|------|
| ✅ PASS | I have logged at least three days of meals | I open the Feedback section | I see at least one specific, actionable recommendation based on my recent nutritional intake |
| ✅ PASS | My average sugar intake has exceeded the recommended daily level for five consecutive days | I view my feedback | The system highlights this pattern and suggests lower-sugar alternatives |
| ❌ FAIL | I have no diary entries for the current week | I open the Feedback section | The system prompts me to log at least one day of meals before personalised feedback can be generated |

---

### Story 4 — Icon-based Nutritional Summary

**As a** subscriber,  
**I can** view my daily nutritional status through icons and colour indicators rather than numbers and tables,  
**So that** I can understand at a glance whether my diet is on track without needing to interpret technical nutritional data.

**Acceptance Criteria:**

| Status | Given | When | Then |
|--------|-------|------|------|
| ✅ PASS | I have logged meals for today | I open the home screen | I see icon-based indicators for key nutrients showing green, amber, or red status depending on whether I am within, approaching, or over the recommended daily amount |
| ✅ PASS | A nutrient indicator is showing red | I tap on the icon | The system displays a brief plain-language explanation of what the status means and a simple suggestion for improvement |
| ❌ FAIL | I have not logged any meals today | I view the home screen | The nutritional status icons are shown in a neutral greyed-out state rather than displaying misleading default values |

---

### Story 5 — Personalised Dietary Suggestions

**As a** subscriber,  
**I can** receive personalised suggestions for healthier food alternatives based on my logged dietary patterns,  
**So that** I can make practical improvements to my diet without having to research healthy options myself.

**Acceptance Criteria:**

| Status | Given | When | Then |
|--------|-------|------|------|
| ✅ PASS | I have regularly logged high-sugar snacks in my diary | I view the Suggestions page | I see at least one recommended lower-sugar alternative relevant to my eating patterns |
| ✅ PASS | A suggestion is displayed | I tap the suggestion card | I see a brief explanation of why this alternative is being recommended based on my recent diary entries |
| ❌ FAIL | I dismiss a suggestion | I return to the Suggestions page within the same day | The dismissed suggestion does not reappear unless my diary entries change significantly |

---

## Section 2 — Subscriber: Home Cooking & Recipes

### Story 6 — Recipe Discovery

**As a** subscriber,  
**I can** browse a library of nutritious meal recommendations with full recipes and preparation instructions,  
**So that** I can discover healthy meals I can cook at home rather than relying on ready meals.

**Acceptance Criteria:**

| Status | Given | When | Then |
|--------|-------|------|------|
| ✅ PASS | I open the Recipes section | The page loads | I see a list of recipe cards each showing a title, thumbnail image, estimated preparation time, and approximate cost |
| ✅ PASS | I tap on a recipe card | The recipe detail page opens | I see the full list of ingredients, step-by-step preparation instructions, and the nutritional profile of the meal |
| ❌ FAIL | I open the Recipes section and the recipe data fails to load due to a network error | The page renders | The system displays an error message and a retry button rather than a blank or broken page |

---

### Story 7 — Ingredient-based Recipe Search

**As a** subscriber,  
**I can** search for recipe recommendations based on specific ingredients I already have,  
**So that** I can find meals I can cook immediately without needing to buy additional items.

**Acceptance Criteria:**

| Status | Given | When | Then |
|--------|-------|------|------|
| ✅ PASS | I enter two ingredients into the ingredient search field | I submit the search | The system returns a list of recipes that use those ingredients, ranked by how many of my listed ingredients they require |
| ✅ PASS | Search results are displayed | I view each recipe card | I can see how many of my entered ingredients the recipe uses without opening the full recipe page |
| ❌ FAIL | I submit an ingredient search with no ingredients entered | The search runs | The system displays a validation message asking me to enter at least one ingredient rather than returning all recipes |

---

### Story 8 — Recipe Ratings and Comments

**As a** subscriber,  
**I can** rate recipes I have tried and read ratings and comments left by other subscribers,  
**So that** I can make informed decisions about which recipes to attempt and share my experience with the community.

**Acceptance Criteria:**

| Status | Given | When | Then |
|--------|-------|------|------|
| ✅ PASS | I have cooked a recipe | I submit a star rating between 1 and 5 and an optional comment | My rating and comment appear on the recipe page with my username and the submission date |
| ✅ PASS | A recipe has received ratings from multiple subscribers | I view the recipe detail page | I see the average star rating and the total number of reviews displayed prominently |
| ❌ FAIL | I attempt to submit a comment that contains only whitespace | I tap Post | The system rejects the submission and displays a message asking me to write a comment before posting |

---

### Story 9 — Recipe Favourites

**As a** subscriber,  
**I can** save recipes I have tried and enjoyed to a personal favourites list,  
**So that** I can quickly find and revisit meals my family or I have approved without searching again.

**Acceptance Criteria:**

| Status | Given | When | Then |
|--------|-------|------|------|
| ✅ PASS | I am viewing a recipe detail page | I tap the Save to Favourites button | The recipe is added to my favourites list immediately and the button state changes to indicate it is saved |
| ✅ PASS | I have saved multiple recipes | I open my Favourites page | All saved recipes are displayed as a grid of cards that I can tap to open |
| ❌ FAIL | A recipe is already in my favourites | I tap the Save to Favourites button again | The system removes the recipe from my favourites and updates the button state rather than adding a duplicate |

---

## Section 3 — Health Professional: Client Management & Advice

### Story 10 — Client Dashboard

**As a** health professional using the service,  
**I can** view a dashboard showing all of my assigned clients and their recent dietary activity,  
**So that** I can monitor how well my clients are following their dietary plans without opening each profile individually.

**Acceptance Criteria:**

| Status | Given | When | Then |
|--------|-------|------|------|
| ✅ PASS | I log in as a health professional | I open my dashboard | I see a list of all assigned clients with each client's name, most recent diary entry date, and a colour-coded nutritional compliance indicator |
| ✅ PASS | I am viewing my dashboard | I click on a specific client's name | I am taken to that client's profile showing their full food diary for the past 30 days |
| ❌ FAIL | I am not authenticated as a health professional | I attempt to access the client dashboard URL directly | I am redirected to the login page and the dashboard content is not displayed |

---

### Story 11 — Client Progress Monitoring

**As a** health professional,  
**I can** observe how well each of my clients is following their nutritional guidelines using visual progress indicators,  
**So that** I can quickly identify which clients need attention without having to read through detailed reports for each one.

**Acceptance Criteria:**

| Status | Given | When | Then |
|--------|-------|------|------|
| ✅ PASS | I am on my client dashboard | The page loads | Each client row displays a visual status icon indicating whether their recent dietary compliance is good, needs attention, or is poor, based on their diary entries for the past seven days |
| ✅ PASS | A client has not met their nutritional guidelines for five or more consecutive days | I view my dashboard | That client's status icon is shown in red to indicate they require follow-up |
| ❌ FAIL | A client has no diary entries in the past seven days | I view my dashboard | Their status icon is shown as inactive rather than defaulting to a misleading compliance score |

---

### Story 12 — Sending Advice and Encouragement

**As a** health professional,  
**I can** send personalised dietary advice and words of encouragement directly to my clients through the system,  
**So that** I can support my clients between appointments without needing to contact them by phone or personal email.

**Acceptance Criteria:**

| Status | Given | When | Then |
|--------|-------|------|------|
| ✅ PASS | I am viewing a client's profile | I type a message in the advice panel and click Send | The message is delivered to the client's inbox and a timestamp is recorded against the sent message |
| ✅ PASS | I have sent a message to a client | The client opens and reads the message | A read receipt is displayed next to the message on my sent messages view |
| ❌ FAIL | I attempt to send a message with an empty text field | I click Send | The system prevents the submission and displays a prompt asking me to write a message before sending |

---

### Story 13 — Automatic Inactivity Alerts

**As a** health professional,  
**I can** receive automatic alerts when a client has not logged their food diary for three or more consecutive days,  
**So that** I can intervene early rather than waiting until the next scheduled appointment to discover that a client has stopped engaging with the service.

**Acceptance Criteria:**

| Status | Given | When | Then |
|--------|-------|------|------|
| ✅ PASS | A client assigned to me has not logged any food diary entries for three consecutive days | The system's daily check runs | An alert flag appears next to that client's name on my dashboard |
| ✅ PASS | An alert is active for a client | I click on the alert | I see the details including the client's name, the number of days without a diary entry, and the date of their last entry |
| ❌ FAIL | I have dismissed an alert for a client | That client logs a new diary entry and then stops again for three days | A new alert is triggered and displayed rather than remaining in the dismissed state |
