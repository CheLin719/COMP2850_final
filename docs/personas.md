# Personas
**Good Food & Healthy Eating — COMP2850**

---

## Persona 1 — Rose
**Subscriber — Media Company Executive Assistant**

### Background
| Field | Detail |
|-------|--------|
| **Name** | Rose |
| **Age** | 31 |
| **Role** | Subscriber |
| **Environment** | Relies entirely on digital channels — apps, websites, and social media — for information, services, and food decisions. Does not engage with printed materials such as recipe books, nutritional leaflets, or paper food diaries. |
| **Demographic** | Executive Assistant at a media company. Lives alone. Long working hours in a high-pressure environment. Frequent takeaway user. |
| **Tech Experience** | Comfortable with smartphones, apps, and online platforms. Accustomed to finding answers and managing daily tasks digitally rather than through physical or offline means. Prefers quick, frictionless digital interactions. |
| **Trigger** | Rose frequently orders takeaway food and consumes high-sugar snacks to cope with stress. She wants to feel more in control of her eating habits but does not want to spend time learning complicated diet rules. |

### Pain Points

**Problem 1 — Stress-driven sweet consumption**
- After stressful workdays, Rose drinks milk tea or eats desserts as a coping mechanism.
- **Currently:** She promises herself to reduce sugar intake the next day, but the cycle keeps repeating.

**Problem 2 — All-or-nothing dieting**
- When concerned about her weight, she tries to drastically cut food intake.
- **Currently:** These extreme attempts are unsustainable and she returns to previous habits within days.

**Problem 3 — Reliance on convenience food**
- Living alone makes takeaway the easiest option after a long day at work.
- **Currently:** She chooses food based on speed and convenience rather than nutritional value.

**Problem 4 — Overwhelmed by nutrition information**
- Most health apps present complex data and detailed charts that feel intimidating.
- **Currently:** She ignores most of the information and only pays attention to the simplest indicators.

### Goals *(what the system must implement)*
- Implement a personalised suggestion feature that recommends healthier alternatives to high-sugar drinks and snacks.
- Implement a food diary that allows users to log meals and track eating patterns over time.
- Provide a simplified nutrition overview that highlights key indicators without overwhelming detail.
- Implement a gradual habit-building tool that sets small, achievable dietary goals rather than extreme restrictions.

### Success Criteria
- Rose receives at least one healthier snack or drink suggestion per day, relevant to her logged food choices.
- She can log a full day of meals in under two minutes without needing to read instructions.
- She can view a simple weekly summary of her eating habits on a single screen without navigating multiple pages.
- She completes at least one small dietary goal per week and the system records her progress over time.

### Constraints
- Very limited time and energy after work — any interaction must be completable in under three minutes.
- High stress levels mean she is likely to abandon features that feel effortful or judgmental.
- Lives alone with no external support for cooking or diet planning.
- Relies entirely on digital channels for food information and services — does not consult printed materials, physical leaflets, or offline resources.

### Non-Goals
- Does not need advanced medical-grade nutritional analysis or calorie calculations.
- Does not want to manage other users' accounts or view health professional reports.
- Not interested in meal prep planning or complex recipe instructions at this stage.

---

## Persona 2 — Dr. James Okafor
**Health Professional — Registered Nutritionist**

### Background
| Field | Detail |
|-------|--------|
| **Name** | Dr. James Okafor |
| **Age** | 47 |
| **Location** | Leeds, UK |
| **Role** | Health Professional |
| **Environment** | Uses a desktop computer at the clinic during consultations and a tablet when reviewing client progress remotely between appointments. |
| **Demographic** | Registered nutritionist running a private practice in Leeds. Manages over 30 subscriber clients across different age groups and dietary needs. Works full-time with back-to-back appointments most days. |
| **Tech Experience** | Proficient with clinical management software and data dashboards. Comfortable with structured digital tools but has no time for systems that require lengthy setup or navigation. |
| **Trigger** | His clinic has joined the platform to support remote dietary monitoring. He needs to oversee all of his clients in one place and send them timely advice without scheduling a call or appointment for every small update. |

### Pain Points

**Problem 1 — No unified view of all clients' dietary progress**
- Client food diary entries are scattered with no aggregated overview, so he must open each profile individually to check their current status.
- **Currently:** He manually reviews each client record one by one, which is time-consuming and means some clients are inadvertently overlooked between appointments.

**Problem 2 — No efficient way to send personalised advice to clients**
- Every piece of advice must be composed and delivered individually, with no in-system communication tool available.
- **Currently:** He contacts clients via personal email or phone, with no record of the communication kept inside the platform.

**Problem 3 — No automatic alert when a client's diet deteriorates or they stop logging**
- There is no mechanism to notify him when a client misses several days of logging or when their nutritional compliance drops significantly.
- **Currently:** He only discovers problems at the next scheduled appointment, by which point the client's habits may have worsened considerably.

### Goals *(what the system must implement)*
- Provide a client management dashboard displaying all assigned clients' recent diary activity and nutritional compliance status in a single view.
- Implement an in-system messaging feature so health professionals can send personalised dietary advice and encouragement directly to clients.
- Implement automatic alerts that notify the professional when a client has not logged food for three or more consecutive days.
- Provide visual progress indicators for each client so the professional can quickly assess who needs attention without opening individual profiles.

### Success Criteria
- He can view all clients' most recent diary date and a nutritional compliance score on a single dashboard page without opening individual profiles.
- He can compose and send a personalised message to a client in under one minute from the client's profile page.
- The system automatically flags a client on his dashboard when they have not logged food for three consecutive days.
- Visual indicators (colour-coded status icons) allow him to identify at-risk clients at a glance without reading detailed reports.

### Constraints
- Subject to GDPR — all client data must be handled securely and accessible only to the assigned professional.
- Back-to-back appointments mean each client review must be completable in under ten minutes.
- Must work reliably on both desktop and tablet without requiring installation of additional software.

### Non-Goals
- Does not need to log his own personal food diary.
- Does not need to browse, rate, or save recipes.
- Does not need to directly edit or modify a client's food diary entries.

---

## Persona 3 — Marco Ferrari
**Subscriber (Home Cook) — Italian Postgraduate Student**

### Background
| Field | Detail |
|-------|--------|
| **Name** | Marco Ferrari |
| **Age** | 27 |
| **Location** | Leeds, UK (originally from Italy) |
| **Role** | Subscriber (Home Cook focus) |
| **Environment** | Uses a tablet propped up in the kitchen while cooking, and his phone in the supermarket to check ingredient lists and costs before buying. |
| **Demographic** | Postgraduate student at the University of Leeds. Lives alone in a shared house. Very tight weekly budget. Beginner-level cooking skills. Currently relies almost entirely on supermarket ready meals. |
| **Tech Experience** | Comfortable with apps and online search. Uses his phone and tablet daily for university work and entertainment. |
| **Trigger** | He has realised that ready meals are both expensive and unhealthy. He wants to start cooking at home but does not know which recipes are affordable, quick, and achievable for someone with limited skills. |

### Pain Points

**Problem 1 — Cannot find recipes that match the ingredients he already has**
- He has several ingredients in his fridge but the system provides no way to search for recipes using only those items.
- **Currently:** He searches the web randomly, finds recipes requiring ingredients he does not have, and ends up buying a ready meal instead.

**Problem 2 — No information on cost or preparation time before opening a recipe**
- He cannot tell from the recipe listing whether a dish fits his budget or time constraints until he has already opened the full page and read through it.
- **Currently:** He wastes time opening and discarding multiple recipes before finding one that might work, and often gives up entirely.

**Problem 3 — No way to know whether a recipe is suitable for a beginner**
- Recipe descriptions and photos give no indication of actual difficulty level or whether the result is worth the effort for someone with limited cooking confidence.
- **Currently:** He attempts recipes at random, sometimes fails, wastes ingredients and money, and gradually loses the motivation to try again.

**Problem 4 — No way to save or revisit recipes he has enjoyed**
- When he does find and cook a recipe he likes, there is no feature to save it for easy access next time.
- **Currently:** He takes a screenshot or tries to remember where he found the recipe, and frequently cannot locate it again.

### Goals *(what the system must implement)*
- Provide an ingredient-based recipe search that returns results ranked by how many of the user's listed ingredients they require.
- Provide recipe cards in the listing view that clearly show estimated preparation time and approximate ingredient cost before the user opens the full recipe.
- Implement a community rating and comment system so users can read honest feedback on difficulty and quality before choosing a recipe.
- Implement a favourites feature so users can save recipes they have tried and enjoyed for easy retrieval later.

### Success Criteria
- He can enter three or fewer ingredients and receive a ranked list of matching recipes within 30 seconds.
- Estimated preparation time and approximate cost are visible on each recipe card without opening the full recipe page.
- He can read at least five community ratings and comments on a recipe before deciding whether to attempt it.
- He can save a recipe to his favourites with a single tap and retrieve it from his favourites page at any time.

### Constraints
- Weekly grocery budget of no more than £30.
- Meals must be preparable in under 30 minutes with basic kitchen equipment.
- Beginner cooking level — recipes must use plain language with no assumed technical knowledge.

### Non-Goals
- Does not need detailed nutritional analysis or diet tracking features.
- Does not need to interact with a health professional or access professional dietary advice.

---

## Persona 4 — Sarah Thompson
**Subscriber — Parent Concerned About Family Nutrition**

### Background
| Field | Detail |
|-------|--------|
| **Name** | Sarah Thompson |
| **Age** | 38 |
| **Location** | Leeds, UK |
| **Role** | Subscriber |
| **Environment** | Uses her phone throughout the day — in the kitchen while cooking, at the supermarket while shopping, and in the evening when planning meals for the week. |
| **Demographic** | Full-time office administrator, married with two children aged 7 and 10. Responsible for most of the family's cooking and food shopping. Concerned about rising childhood obesity rates in the UK. |
| **Tech Experience** | Comfortable with smartphones and apps used daily. Prefers visual, icon-driven interfaces over text-heavy pages — does not want to read lengthy instructions or nutritional tables. |
| **Trigger** | After reading about increasing childhood obesity rates in the UK, Sarah wants to improve her family's diet but does not know where to start. She currently cooks mostly from habit and convenience, with no clear sense of whether her family's meals meet nutritional guidelines. |

### Pain Points

**Problem 1 — No simple way to check whether family meals meet nutritional guidelines**
- She has no tool to assess whether the meals she cooks contain the right balance of nutrients for children and adults.
- **Currently:** She relies on rough guesswork and general awareness, with no clear feedback on whether her family's diet is actually healthy.

**Problem 2 — Cannot find healthy family-friendly recipes quickly**
- Most recipe platforms are not tailored to families with children — recipes are either too complex, too expensive, or not nutritionally focused.
- **Currently:** She falls back on the same small rotation of meals her family already accepts, missing the opportunity to introduce healthier options.

**Problem 3 — Nutritional information is presented in a format she cannot easily interpret**
- Nutrition labels and charts use numbers and terminology that require effort to understand, which she does not have time for during a busy day.
- **Currently:** She ignores detailed nutritional breakdowns and relies on vague impressions of whether a meal is 'healthy enough'.

**Problem 4 — No record of which recipes her family has tried and approved**
- When her children enjoy a particular meal, she has no efficient way to save and retrieve that recipe for future use.
- **Currently:** She bookmarks pages in a browser or photographs recipes on her phone, resulting in a disorganised collection she rarely revisits.

### Goals *(what the system must implement)*
- Implement a food diary and nutritional tracking feature that provides clear visual feedback on whether daily meals meet recommended dietary guidelines.
- Provide icon-based nutritional summaries that communicate dietary information visually without requiring users to interpret numbers or technical terms.
- Provide a searchable recipe library that includes family-friendly, nutritious meals with clear preparation times and cost estimates.
- Implement a favourites feature so users can save approved family recipes and retrieve them easily for future meal planning.

### Success Criteria
- She can log a family meal and immediately see a clear visual indicator of whether it meets the recommended nutritional guidelines for that day.
- Nutritional feedback is communicated through icons and colour coding so she understands her family's dietary status without reading numbers or tables.
- She can find at least three family-friendly healthy recipes within two minutes using the search feature.
- She can save a recipe to favourites in one tap and access her full saved list from a dedicated page.

### Constraints
- Busy daily schedule with children — interactions must be quick and require no prior reading or training.
- Needs an interface that relies on icons and visuals rather than text, so it is accessible during rushed moments.
- Family budget constraints mean recipes must be practically affordable for a family of four.

### Non-Goals
- Does not need to interact with a health professional or receive clinical dietary advice.
- Does not need advanced sports nutrition or fitness-specific tracking features.
- Does not need to manage accounts for other users or view professional dashboards.
