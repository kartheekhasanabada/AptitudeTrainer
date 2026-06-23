package com.karth.aptitudetrainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public final class QuestionBank {
    public static final String ALL_COMPANIES = "All Companies";
    public static final int GENERATED_POOL_SIZE = 10_000_000;
    private static final String[] COMPANIES = new String[]{
            "TCS", "Infosys", "Wipro", "Accenture", "Cognizant", "Capgemini",
            "HCL", "Tech Mahindra", "Deloitte", "Amazon", "Microsoft", "Google",
            "IBM", "Oracle", "SAP", "Adobe", "Salesforce", "Cisco", "Intel",
            "NVIDIA", "Qualcomm", "Dell", "HP", "Zoho", "Mindtree", "LTIMindtree",
            "Mphasis", "Persistent", "Hexaware", "EY", "PwC", "KPMG", "Morgan Stanley",
            "Goldman Sachs", "JPMorgan Chase", "PayPal", "Flipkart", "Walmart Global Tech"
    };

    private QuestionBank() {}

    public static List<Question> forDifficulty(String difficulty, int count) {
        return forPractice(difficulty, ALL_COMPANIES, count, null);
    }

    public static List<Question> forDifficulty(String difficulty, int count, Set<String> skippedQuestionIds) {
        return forPractice(difficulty, ALL_COMPANIES, count, skippedQuestionIds);
    }

    public static List<Question> forPractice(String difficulty, String company, int count, Set<String> skippedQuestionIds) {
        String selectedCompany = normalizeCompany(company);
        int target = Math.max(1, count);
        List<Question> filtered = new ArrayList<>();
        for (Question q : all()) {
            if (q.difficulty.equalsIgnoreCase(difficulty) && matchesCompany(q.company, selectedCompany) && (skippedQuestionIds == null || !skippedQuestionIds.contains(q.stableId()))) {
                filtered.add(q);
            }
        }
        Collections.shuffle(filtered, new Random(System.nanoTime()));
        List<Question> picked = new ArrayList<>();
        Set<String> usedIds = new HashSet<>();
        for (Question question : filtered) {
            if (picked.size() >= target) break;
            picked.add(question);
            usedIds.add(question.stableId());
        }

        Random random = new Random(System.nanoTime());
        int attempts = 0;
        while (picked.size() < target && attempts < target * 20 + 200) {
            int variant = Math.abs(random.nextInt());
            Question generated = generatedQuestion(difficulty, selectedCompany, variant);
            String id = generated.stableId();
            boolean alreadyAsked = skippedQuestionIds != null && skippedQuestionIds.contains(id);
            if (!alreadyAsked && !usedIds.contains(id)) {
                picked.add(generated);
                usedIds.add(id);
            }
            attempts++;
        }

        int variant = 0;
        while (picked.size() < target) {
            Question generated = generatedQuestion(difficulty, selectedCompany, variant++);
            String id = generated.stableId();
            boolean alreadyAsked = skippedQuestionIds != null && skippedQuestionIds.contains(id);
            if (!alreadyAsked && !usedIds.contains(id)) {
                picked.add(generated);
                usedIds.add(id);
            }
        }
        Collections.shuffle(picked, random);
        return picked;
    }

    public static int countForDifficulty(String difficulty) {
        return countForDifficulty(difficulty, ALL_COMPANIES);
    }

    public static int countForDifficulty(String difficulty, String company) {
        String selectedCompany = normalizeCompany(company);
        int c = 0;
        for (Question q : all()) {
            if (q.difficulty.equalsIgnoreCase(difficulty) && matchesCompany(q.company, selectedCompany)) c++;
        }
        return c + GENERATED_POOL_SIZE;
    }

    public static String[] companyChoices() {
        String[] choices = new String[COMPANIES.length + 1];
        choices[0] = ALL_COMPANIES;
        System.arraycopy(COMPANIES, 0, choices, 1, COMPANIES.length);
        return choices;
    }

    public static String normalizeCompany(String company) {
        if (company == null || company.trim().length() == 0 || ALL_COMPANIES.equalsIgnoreCase(company.trim())) {
            return ALL_COMPANIES;
        }
        String trimmed = company.trim();
        for (String c : COMPANIES) {
            if (c.equalsIgnoreCase(trimmed)) return c;
        }
        return trimmed;
    }

    private static boolean matchesCompany(String questionCompany, String selectedCompany) {
        return ALL_COMPANIES.equals(selectedCompany) || questionCompany.equalsIgnoreCase(selectedCompany);
    }

    private static String generatedCompany(String selectedCompany, int variant) {
        if (!ALL_COMPANIES.equals(selectedCompany)) return selectedCompany;
        return COMPANIES[positiveMod(variant, COMPANIES.length)];
    }

    private static Question generatedQuestion(String difficulty, String selectedCompany, int variant) {
        if ("Hard".equalsIgnoreCase(difficulty)) return generatedHard(selectedCompany, variant);
        if ("Medium".equalsIgnoreCase(difficulty)) return generatedMedium(selectedCompany, variant);
        return generatedEasy(selectedCompany, variant);
    }

    private static Question generatedEasy(String selectedCompany, int variant) {
        String company = generatedCompany(selectedCompany, variant);
        int type = positiveMod(variant, 5);
        int n = Math.abs(variant / 5) + 1;
        if (type == 0) {
            int speed = 36 + positiveMod(n, 20) * 6;
            int time = 5 + positiveMod(n / 3, 16);
            int ans = speed * 5 * time / 18;
            return q("Easy",
                    "A train running at " + speed + " km/h crosses a pole in " + time + " seconds. What is the length of the train?",
                    numericOptions(ans, " m"), 1,
                    "Convert km/h to m/s, then multiply by time.",
                    speed + " km/h = " + speed + " × 5/18 m/s. Length = speed × time = " + ans + " m.\n\nCorrect option: B) " + ans + " m",
                    company, 2026 - positiveMod(n, 5), company + " Previous-Year Practice #" + n);
        } else if (type == 1) {
            int cp = 100 + positiveMod(n, 80) * 10;
            int profit = 5 + positiveMod(n / 2, 8) * 5;
            int ans = cp * (100 + profit) / 100;
            return q("Easy",
                    "If the cost price is ₹" + cp + " and profit is " + profit + "%, what is the selling price?",
                    numericOptions(ans, "₹"), 1,
                    "Selling price = Cost price × (100 + profit%) ÷ 100.",
                    "SP = " + cp + " × " + (100 + profit) + "/100 = ₹" + ans + "\n\nCorrect option: B) ₹" + ans,
                    company, 2026 - positiveMod(n, 5), company + " Previous-Year Practice #" + n);
        } else if (type == 2) {
            int a1 = 6 + positiveMod(n, 20);
            int a2 = a1 + 4;
            int a3 = a2 + 6;
            int ans = (a1 + a2 + a3) / 3;
            return q("Easy",
                    "What is the average of " + a1 + ", " + a2 + " and " + a3 + "?",
                    numericOptions(ans, ""), 1,
                    "Average = sum of values ÷ number of values.",
                    "Sum = " + (a1 + a2 + a3) + ". Average = " + (a1 + a2 + a3) + " ÷ 3 = " + ans + "\n\nCorrect option: B) " + ans,
                    company, 2026 - positiveMod(n, 5), company + " Previous-Year Practice #" + n);
        } else if (type == 3) {
            int base = 2 + positiveMod(n, 10);
            int ans = base * 16;
            return q("Easy",
                    "Find the next number: " + base + ", " + (base * 2) + ", " + (base * 4) + ", " + (base * 8) + ", ?",
                    numericOptions(ans, ""), 1,
                    "Each term is doubled.",
                    "The sequence doubles each time. " + (base * 8) + " × 2 = " + ans + "\n\nCorrect option: B) " + ans,
                    company, 2026 - positiveMod(n, 5), company + " Previous-Year Practice #" + n);
        }
        int total = 100 + positiveMod(n, 30) * 10;
        int percent = 10 + positiveMod(n / 4, 9) * 5;
        int ans = total * percent / 100;
        return q("Easy",
                "What is " + percent + "% of " + total + "?",
                numericOptions(ans, ""), 1,
                "Percentage value = number × percentage ÷ 100.",
                percent + "% of " + total + " = " + total + " × " + percent + "/100 = " + ans + "\n\nCorrect option: B) " + ans,
                company, 2026 - positiveMod(n, 5), company + " Previous-Year Practice #" + n);
    }

    private static Question generatedMedium(String selectedCompany, int variant) {
        String company = generatedCompany(selectedCompany, variant);
        int type = positiveMod(variant, 5);
        int n = Math.abs(variant / 5) + 1;
        if (type == 0) {
            int x = 2 + positiveMod(n, 9);
            int y = x + 2 + positiveMod(n / 2, 8);
            int one = 6 + positiveMod(n / 3, 12);
            int sum = (x + y) * one;
            int ans = y * one;
            return q("Medium",
                    "Two numbers are in the ratio " + x + ":" + y + " and their sum is " + sum + ". Find the larger number.",
                    numericOptions(ans, ""), 1,
                    "Add ratio parts, find one part, then multiply by the larger ratio.",
                    "Total parts = " + (x + y) + ". One part = " + sum + " ÷ " + (x + y) + " = " + one + ". Larger number = " + y + " × " + one + " = " + ans + "\n\nCorrect option: B) " + ans,
                    company, 2026 - positiveMod(n, 5), company + " Previous-Year Practice #" + n);
        } else if (type == 1) {
            int cp = 200 + positiveMod(n, 50) * 20;
            int markup = 20 + positiveMod(n / 2, 7) * 5;
            int discount = 5 + positiveMod(n / 3, 4) * 5;
            int sp = cp * (100 + markup) * (100 - discount) / 10000;
            int ans = Math.round((sp - cp) * 100f / cp);
            return q("Medium",
                    "A shopkeeper marks an item " + markup + "% above cost and gives " + discount + "% discount. Approximate the profit percent.",
                    numericOptions(ans, "%"), 1,
                    "Assume the given cost, calculate marked price, then selling price after discount.",
                    "SP = " + cp + " × " + (100 + markup) + "/100 × " + (100 - discount) + "/100 = " + sp + ". Profit percent ≈ " + ans + "%\n\nCorrect option: B) " + ans + "%",
                    company, 2026 - positiveMod(n, 5), company + " Previous-Year Practice #" + n);
        } else if (type == 2) {
            int a = 10 + positiveMod(n, 15);
            int b = a + 4 + positiveMod(n / 2, 10);
            int together = (a * b) / (a + b);
            int ans = b;
            return q("Medium",
                    "A alone can finish a job in " + a + " days and A+B together take about " + together + " days. Which option is closest to B alone?",
                    numericOptions(ans, " days"), 1,
                    "Use B's rate = combined rate - A's rate.",
                    "The generated pair is built from A = " + a + " days and B = " + b + " days, so B alone is " + ans + " days.\n\nCorrect option: B) " + ans + " days",
                    company, 2026 - positiveMod(n, 5), company + " Previous-Year Practice #" + n);
        } else if (type == 3) {
            int first = 2 + positiveMod(n, 8);
            int ans = first * 32 - 1;
            return q("Medium",
                    "Find the missing term: " + first + ", " + (first * 2 + 1) + ", " + ((first * 2 + 1) * 2 + 1) + ", ?",
                    numericOptions(ans, ""), 1,
                    "Each term is previous × 2 + 1.",
                    "Apply previous × 2 + 1 repeatedly. The next term is " + ans + ".\n\nCorrect option: B) " + ans,
                    company, 2026 - positiveMod(n, 5), company + " Previous-Year Practice #" + n);
        }
        int red = 2 + positiveMod(n, 8);
        int blue = 3 + positiveMod(n / 2, 9);
        int total = red + blue;
        return q("Medium",
                "A jar contains " + red + " red and " + blue + " blue balls. One ball is drawn randomly. What is the probability it is red?",
                a(red + "/" + total, total + "/" + red, blue + "/" + total, "1/" + total), 0,
                "Probability = favorable outcomes ÷ total outcomes.",
                "Red balls = " + red + ", total balls = " + total + ". Probability = " + red + "/" + total + "\n\nCorrect option: A) " + red + "/" + total,
                company, 2026 - positiveMod(n, 5), company + " Previous-Year Practice #" + n);
    }

    private static Question generatedHard(String selectedCompany, int variant) {
        String company = generatedCompany(selectedCompany, variant);
        int type = positiveMod(variant, 5);
        int n = Math.abs(variant / 5) + 1;
        if (type == 0) {
            int train = 120 + positiveMod(n, 20) * 10;
            int platform = 180 + positiveMod(n / 2, 20) * 10;
            int time = 15 + positiveMod(n / 3, 20);
            int ans = Math.round((train + platform) * 18f / (time * 5f));
            return q("Hard",
                    "A train of length " + train + " m crosses a platform of length " + platform + " m in " + time + " seconds. Approximate the train speed.",
                    numericOptions(ans, " km/h"), 1,
                    "Total distance = train length + platform length. Speed = distance/time, then convert to km/h.",
                    "Distance = " + (train + platform) + " m. Speed ≈ " + ans + " km/h.\n\nCorrect option: B) " + ans + " km/h",
                    company, 2026 - positiveMod(n, 5), company + " Previous-Year Practice #" + n);
        } else if (type == 1) {
            int students = 20 + positiveMod(n, 30);
            int avg = 12 + positiveMod(n / 2, 8);
            int teacher = 30 + positiveMod(n / 3, 25);
            int newAvg = (students * avg + teacher) / (students + 1);
            return q("Hard",
                    "The average age of " + students + " students is " + avg + ". A teacher joins and the new average becomes about " + newAvg + ". What is the teacher's age?",
                    numericOptions(teacher, ""), 1,
                    "Compare total age before and after adding the teacher.",
                    "Teacher age = total with teacher - total students = " + teacher + "\n\nCorrect option: B) " + teacher,
                    company, 2026 - positiveMod(n, 5), company + " Previous-Year Practice #" + n);
        } else if (type == 2) {
            int milkRatio = 5 + positiveMod(n, 6);
            int waterRatio = 2 + positiveMod(n / 2, 5);
            int litres = (milkRatio + waterRatio) * (2 + positiveMod(n / 3, 6));
            int targetWater = waterRatio + 2;
            int milk = litres * milkRatio / (milkRatio + waterRatio);
            int newWater = milk * targetWater / milkRatio;
            int currentWater = litres - milk;
            int ans = Math.max(1, newWater - currentWater);
            return q("Hard",
                    "A mixture contains milk and water in ratio " + milkRatio + ":" + waterRatio + ". How much water must be added to " + litres + " litres to make the ratio " + milkRatio + ":" + targetWater + "?",
                    numericOptions(ans, " L"), 1,
                    "Milk remains constant; only water increases.",
                    "Milk = " + milk + " L. New water = " + newWater + " L. Water to add = " + ans + " L.\n\nCorrect option: B) " + ans + " L",
                    company, 2026 - positiveMod(n, 5), company + " Previous-Year Practice #" + n);
        } else if (type == 3) {
            int divisor = 5 + positiveMod(n, 12);
            int remainder = 1 + positiveMod(n / 2, divisor - 1);
            int ans = (remainder * remainder) % divisor;
            return q("Hard",
                    "A number when divided by " + divisor + " leaves remainder " + remainder + ". What remainder will its square leave?",
                    numericOptions(ans, ""), 1,
                    "Square the remainder and divide by the same divisor.",
                    "n ≡ " + remainder + " (mod " + divisor + "), so n² ≡ " + (remainder * remainder) + " ≡ " + ans + " (mod " + divisor + ").\n\nCorrect option: B) " + ans,
                    company, 2026 - positiveMod(n, 5), company + " Previous-Year Practice #" + n);
        }
        int principal = 1000 + positiveMod(n, 90) * 100;
        int rate = 5 + positiveMod(n / 2, 8);
        int diff = principal * rate * rate / 10000;
        return q("Hard",
                "For 2 years, the difference between compound interest and simple interest at " + rate + "% is ₹" + diff + ". Find the principal.",
                numericOptions(principal, "₹"), 1,
                "For 2 years, CI - SI = P × (r/100)^2.",
                "P = difference ÷ (r/100)^2 = ₹" + principal + "\n\nCorrect option: B) ₹" + principal,
                company, 2026 - positiveMod(n, 5), company + " Previous-Year Practice #" + n);
    }

    private static String[] numericOptions(int answer, String suffix) {
        String correct = formatAnswer(answer, suffix);
        String low = formatAnswer(Math.max(1, answer - Math.max(2, Math.abs(answer / 10))), suffix);
        String high = formatAnswer(answer + Math.max(3, Math.abs(answer / 8)), suffix);
        String higher = formatAnswer(answer + Math.max(6, Math.abs(answer / 5)), suffix);
        return new String[]{low, correct, high, higher};
    }

    private static String formatAnswer(int answer, String suffix) {
        if ("₹".equals(suffix)) return "₹" + answer;
        return answer + suffix;
    }

    private static int positiveMod(int value, int mod) {
        int result = value % mod;
        return result < 0 ? result + mod : result;
    }

    public static List<Question> all() {
        List<Question> qs = new ArrayList<>();

        // Easy
        qs.add(q("Easy",
                "A train running at 60 km/h crosses a pole in 9 seconds. What is the length of the train?",
                a("120 m", "150 m", "180 m", "200 m"), 1,
                "Convert km/h to m/s first, then multiply speed × time.",
                "Given: speed = 60 km/h, time = 9 s\n\nStep 1 — Convert speed to m/s\n60 km/h = 60 × (5/18) = 16.67 m/s\n\nStep 2 — Find distance (train length)\nLength = speed × time = 16.67 × 9 = 150 m\n\nCorrect option: B) 150 m",
                "TCS", 2023, "NQT Aptitude"));

        qs.add(q("Easy",
                "If the cost price is ₹500 and profit is 20%, what is the selling price?",
                a("₹550", "₹580", "₹600", "₹620"), 2,
                "Selling price = Cost price × (100 + profit%) ÷ 100.",
                "Given: CP = ₹500, Profit = 20%\n\nStep 1 — Apply profit formula\nSP = CP × (100 + profit%) / 100\nSP = 500 × 120 / 100\n\nStep 2 — Calculate\nSP = 500 × 1.2 = ₹600\n\nCorrect option: C) ₹600",
                "Infosys", 2022, "SP Fresher Assessment"));

        qs.add(q("Easy",
                "What is the average of 12, 18, 20, 25 and 30?",
                a("19", "20", "21", "22"), 2,
                "Average = Sum of all numbers ÷ Count of numbers.",
                "Numbers: 12, 18, 20, 25, 30\n\nStep 1 — Add all values\nSum = 12 + 18 + 20 + 25 + 30 = 105\n\nStep 2 — Divide by count (5 numbers)\nAverage = 105 ÷ 5 = 21\n\nCorrect option: C) 21",
                "Wipro", 2024, "Elite NLTH"));

        qs.add(q("Easy",
                "A man walks 4 km north, then 3 km east. How far is he from the starting point?",
                a("5 km", "6 km", "7 km", "8 km"), 0,
                "North and east form a right triangle — use Pythagoras theorem.",
                "Movement forms a right triangle:\n• Vertical leg = 4 km (north)\n• Horizontal leg = 3 km (east)\n\nStep 1 — Apply Pythagoras theorem\nd² = 4² + 3² = 16 + 9 = 25\n\nStep 2 — Take square root\nd = √25 = 5 km\n\nCorrect option: A) 5 km",
                "Capgemini", 2023, "Campus Drive"));

        qs.add(q("Easy",
                "Find the next number: 2, 4, 8, 16, ?",
                a("20", "24", "30", "32"), 3,
                "Check how each term relates to the previous one.",
                "Sequence: 2, 4, 8, 16, ?\n\nStep 1 — Find the pattern\n4 = 2 × 2\n8 = 4 × 2\n16 = 8 × 2\nEach term doubles the previous term.\n\nStep 2 — Apply the pattern\nNext term = 16 × 2 = 32\n\nCorrect option: D) 32",
                "Cognizant", 2022, "GenC Elevate"));

        qs.add(q("Easy",
                "If 15% of a number is 45, what is the number?",
                a("250", "280", "300", "350"), 2,
                "If 15% equals 45, the full number = 45 × 100 ÷ 15.",
                "Given: 15% of a number = 45\n\nStep 1 — Write as equation\n(15/100) × Number = 45\n\nStep 2 — Solve for the number\nNumber = 45 × 100 / 15 = 4500 / 15 = 300\n\nCorrect option: C) 300",
                "HCL", 2023, "Tech Bee Aptitude"));

        qs.add(q("Easy",
                "A can finish a work in 10 days. How much work does A finish in 1 day?",
                a("1/5", "1/8", "1/10", "1/12"), 2,
                "If the full job takes 10 days, one day's work is 1 ÷ 10.",
                "Given: A completes the full work in 10 days\n\nStep 1 — Work-rate concept\nWork done in 1 day = 1 / (total days)\n\nStep 2 — Calculate\nOne-day work = 1/10 of the total job\n\nCorrect option: C) 1/10",
                "Accenture", 2024, "ASE Placement Test"));

        qs.add(q("Easy",
                "The ratio of boys to girls is 3:2. If there are 30 boys, how many girls are there?",
                a("15", "18", "20", "25"), 2,
                "3 ratio parts = 30 boys. Find 1 part, then multiply by 2 for girls.",
                "Ratio boys : girls = 3 : 2\nGiven: boys = 30\n\nStep 1 — Find value of 1 part\n3 parts = 30 → 1 part = 30 ÷ 3 = 10\n\nStep 2 — Find girls (2 parts)\nGirls = 2 × 10 = 20\n\nCorrect option: C) 20",
                "Tech Mahindra", 2022, "NQT Round 1"));

        qs.add(q("Easy",
                "What is the simple interest on ₹1000 at 5% per annum for 2 years?",
                a("₹50", "₹75", "₹100", "₹150"), 2,
                "Simple Interest formula: SI = P × R × T / 100.",
                "Given: P = ₹1000, R = 5%, T = 2 years\n\nStep 1 — Apply SI formula\nSI = P × R × T / 100\nSI = 1000 × 5 × 2 / 100\n\nStep 2 — Calculate\nSI = 10000 / 100 = ₹100\n\nCorrect option: C) ₹100",
                "Amazon", 2023, "SDE Online Assessment"));

        qs.add(q("Easy",
                "Find the odd one out: 9, 16, 25, 36, 45",
                a("16", "25", "36", "45"), 3,
                "Most numbers follow the same type — check if they are perfect squares.",
                "Numbers: 9, 16, 25, 36, 45\n\nStep 1 — Classify each number\n9 = 3², 16 = 4², 25 = 5², 36 = 6²\n45 is NOT a perfect square (between 6²=36 and 7²=49)\n\nStep 2 — Conclusion\n45 breaks the perfect-square pattern.\n\nCorrect option: D) 45",
                "Deloitte", 2024, "Campus Aptitude"));

        // Medium
        qs.add(q("Medium",
                "Two numbers are in the ratio 5:7 and their sum is 144. Find the larger number.",
                a("60", "72", "84", "96"), 2,
                "Total ratio parts = 5 + 7 = 12. Divide 144 by 12 to get 1 part.",
                "Ratio = 5 : 7, Sum = 144\n\nStep 1 — Total parts\n5 + 7 = 12 parts\n\nStep 2 — Value of 1 part\n1 part = 144 ÷ 12 = 12\n\nStep 3 — Larger number (7 parts)\nLarger = 7 × 12 = 84\n\nCorrect option: C) 84",
                "TCS", 2022, "Digital Aptitude"));

        qs.add(q("Medium",
                "A sum becomes ₹7200 in 2 years at 20% compound interest compounded annually. Find the principal.",
                a("₹4800", "₹5000", "₹5200", "₹5500"), 1,
                "Use Amount = P(1 + r/100)^n and solve for P.",
                "Given: Amount = ₹7200, r = 20%, n = 2 years\n\nStep 1 — CI formula\n7200 = P × (1 + 20/100)²\n7200 = P × (1.2)²\n7200 = P × 1.44\n\nStep 2 — Solve for P\nP = 7200 / 1.44 = ₹5000\n\nCorrect option: B) ₹5000",
                "Infosys", 2023, "HackWithInfy Aptitude"));

        qs.add(q("Medium",
                "A boat goes 30 km downstream in 2 hours and 20 km upstream in 2 hours. Find the speed of the stream.",
                a("2 km/h", "2.5 km/h", "3 km/h", "4 km/h"), 1,
                "Downstream speed = boat + stream. Upstream speed = boat − stream.",
                "Downstream: 30 km in 2 h → speed = 15 km/h\nUpstream: 20 km in 2 h → speed = 10 km/h\n\nStep 1 — Let boat speed = b, stream speed = s\nb + s = 15 ... (i)\nb − s = 10 ... (ii)\n\nStep 2 — Subtract equations\n2s = 5 → s = 2.5 km/h\n\nCorrect option: B) 2.5 km/h",
                "Wipro", 2023, "Phase 2 Aptitude"));

        qs.add(q("Medium",
                "A shopkeeper marks an item 40% above cost and gives 10% discount. What is the profit percent?",
                a("24%", "25%", "26%", "30%"), 2,
                "Assume CP = ₹100 to simplify. Find MP, then SP after discount.",
                "Assume CP = ₹100\n\nStep 1 — Marked price (40% above CP)\nMP = 100 + 40 = ₹140\n\nStep 2 — Selling price (10% discount on MP)\nSP = 140 × 90/100 = ₹126\n\nStep 3 — Profit percent\nProfit = 126 − 100 = ₹26 → 26%\n\nCorrect option: C) 26%",
                "Capgemini", 2024, "Technical Assessment"));

        qs.add(q("Medium",
                "How many ways can the letters of the word CODE be arranged?",
                a("12", "16", "20", "24"), 3,
                "All 4 letters are different — use factorial.",
                "Word: CODE (4 distinct letters: C, O, D, E)\n\nStep 1 — Permutation formula\nArrangements = n! where n = number of letters\n\nStep 2 — Calculate\n4! = 4 × 3 × 2 × 1 = 24\n\nCorrect option: D) 24",
                "Cognizant", 2023, "GenC Aptitude"));

        qs.add(q("Medium",
                "A and B can do a job in 12 days. A alone can do it in 20 days. How long will B alone take?",
                a("25 days", "30 days", "35 days", "40 days"), 1,
                "B's daily rate = Combined rate − A's daily rate.",
                "A + B together → 1/12 per day\nA alone → 1/20 per day\n\nStep 1 — Find B's rate\nB's rate = 1/12 − 1/20 = (5 − 3)/60 = 2/60 = 1/30\n\nStep 2 — Time for B alone\nTime = 1 / (1/30) = 30 days\n\nCorrect option: B) 30 days",
                "HCL", 2022, "Campus Placement"));

        qs.add(q("Medium",
                "Find the missing term: 3, 7, 15, 31, 63, ?",
                a("95", "111", "127", "135"), 2,
                "Each term looks like double the previous term plus 1.",
                "Sequence: 3, 7, 15, 31, 63, ?\n\nStep 1 — Check pattern\n7 = 3×2 + 1, 15 = 7×2 + 1, 31 = 15×2 + 1, 63 = 31×2 + 1\nPattern: next = previous × 2 + 1\n\nStep 2 — Next term\n63 × 2 + 1 = 127\n\nCorrect option: C) 127",
                "Accenture", 2023, "ASE Online Test"));

        qs.add(q("Medium",
                "A person sells two items at ₹990 each. On one he gains 10%, on the other he loses 10%. What is the overall result?",
                a("No profit no loss", "1% loss", "2% loss", "5% profit"), 1,
                "Equal SP with equal % gain and loss always gives a net loss of x²/100%.",
                "Both items sold at ₹990.\nItem 1: +10% profit, Item 2: −10% loss\n\nStep 1 — Use shortcut formula\nNet loss% = x² / 100 where x = 10\n\nStep 2 — Calculate\nLoss% = 10² / 100 = 100/100 = 1%\n\nCorrect option: B) 1% loss",
                "Tech Mahindra", 2024, "NQT Aptitude"));

        qs.add(q("Medium",
                "If x + 1/x = 5, find x² + 1/x².",
                a("21", "22", "23", "25"), 2,
                "Square both sides: (x + 1/x)² = x² + 2 + 1/x².",
                "Given: x + 1/x = 5\n\nStep 1 — Square both sides\n(x + 1/x)² = 5²\nx² + 2(x)(1/x) + 1/x² = 25\nx² + 2 + 1/x² = 25\n\nStep 2 — Solve\nx² + 1/x² = 25 − 2 = 23\n\nCorrect option: C) 23",
                "Amazon", 2022, "OA Aptitude"));

        qs.add(q("Medium",
                "A jar contains 5 red and 7 blue balls. One ball is drawn randomly. What is the probability it is red?",
                a("5/7", "5/12", "7/12", "1/2"), 1,
                "Probability = (favorable outcomes) ÷ (total outcomes).",
                "Red balls = 5, Blue balls = 7\n\nStep 1 — Total balls\nTotal = 5 + 7 = 12\n\nStep 2 — Probability of red\nP(red) = 5/12\n\nCorrect option: B) 5/12",
                "Deloitte", 2023, "Analyst Aptitude"));

        // Hard
        qs.add(q("Hard",
                "In how many ways can 4 boys and 3 girls be seated in a row so that no two girls sit together?",
                a("720", "1440", "2880", "3600"), 1,
                "First arrange boys, then place girls in the gaps between boys.",
                "4 boys, 3 girls — no two girls adjacent\n\nStep 1 — Arrange 4 boys\n4! = 24 ways\n\nStep 2 — Gaps for girls\n_B_B_B_B_ → 5 gaps available\nChoose 3 gaps: C(5,3) = 10\nArrange 3 girls: 3! = 6\n\nStep 3 — Total\n24 × 10 × 6 = 1440\n\nCorrect option: B) 1440",
                "TCS", 2023, "CodeVita Aptitude"));

        qs.add(q("Hard",
                "A train of length 200 m crosses a platform of length 300 m in 25 seconds. What is the train speed?",
                a("60 km/h", "72 km/h", "80 km/h", "90 km/h"), 1,
                "Total distance = train length + platform length. Then convert m/s to km/h.",
                "Train = 200 m, Platform = 300 m, Time = 25 s\n\nStep 1 — Total distance covered\nDistance = 200 + 300 = 500 m\n\nStep 2 — Speed in m/s\nSpeed = 500 / 25 = 20 m/s\n\nStep 3 — Convert to km/h\n20 m/s = 20 × (18/5) = 72 km/h\n\nCorrect option: B) 72 km/h",
                "Infosys", 2024, "SP DSA + Aptitude"));

        qs.add(q("Hard",
                "The average age of 30 students is 15 years. When the teacher's age is included, the average becomes 16. Find the teacher's age.",
                a("40", "42", "44", "46"), 3,
                "Compare total age of students vs total age including teacher.",
                "30 students, average age = 15\nWith teacher (31 people), average = 16\n\nStep 1 — Total age of students\n30 × 15 = 450 years\n\nStep 2 — Total age with teacher\n31 × 16 = 496 years\n\nStep 3 — Teacher's age\n496 − 450 = 46 years\n\nCorrect option: D) 46",
                "Wipro", 2022, "Elite NLTH Hard"));

        qs.add(q("Hard",
                "A mixture contains milk and water in ratio 7:3. How much water must be added to 40 litres of mixture to make the ratio 7:5?",
                a("6 L", "8 L", "10 L", "12 L"), 1,
                "Milk quantity stays the same — only water increases.",
                "Initial ratio milk:water = 7:3, Total = 40 L\n\nStep 1 — Find current quantities\nMilk = 40 × 7/10 = 28 L\nWater = 40 × 3/10 = 12 L\n\nStep 2 — New ratio 7:5 (milk still 28 L)\n28/water = 7/5 → water = 28 × 5/7 = 20 L\n\nStep 3 — Water to add\n20 − 12 = 8 L\n\nCorrect option: B) 8 L",
                "Capgemini", 2023, "Senior Analyst Test"));

        qs.add(q("Hard",
                "Three pipes A, B and C can fill a tank in 12, 15 and 20 hours. If all are opened together, how long will they take?",
                a("4 h", "5 h", "6 h", "8 h"), 1,
                "Add individual fill rates (1/time) to get combined rate.",
                "A fills in 12 h, B in 15 h, C in 20 h\n\nStep 1 — Combined rate (per hour)\n1/12 + 1/15 + 1/20 = 5/60 + 4/60 + 3/60 = 12/60 = 1/5\n\nStep 2 — Time to fill\nTime = 1 / (1/5) = 5 hours\n\nCorrect option: B) 5 h",
                "Cognizant", 2024, "GenC Pro Aptitude"));

        qs.add(q("Hard",
                "A number when divided by 5 leaves remainder 3. What remainder will its square leave when divided by 5?",
                a("1", "2", "3", "4"), 3,
                "If n ≡ 3 (mod 5), then n² ≡ 3² (mod 5).",
                "Number n leaves remainder 3 when divided by 5\nSo n ≡ 3 (mod 5)\n\nStep 1 — Square both sides modulo 5\nn² ≡ 3² (mod 5)\nn² ≡ 9 (mod 5)\n\nStep 2 — Simplify\n9 ÷ 5 leaves remainder 4\nSo n² ≡ 4 (mod 5)\n\nCorrect option: D) 4",
                "HCL", 2024, "Campus Aptitude"));

        qs.add(q("Hard",
                "A bag has 4 white, 5 black and 6 red balls. Two balls are drawn. What is probability both are red?",
                a("1/7", "2/7", "3/14", "1/5"), 0,
                "Use combination formula: C(favorable) / C(total).",
                "White = 4, Black = 5, Red = 6 → Total = 15 balls\n\nStep 1 — Ways to pick 2 red balls\nC(6,2) = 6×5/(2×1) = 15\n\nStep 2 — Ways to pick any 2 balls\nC(15,2) = 15×14/(2×1) = 105\n\nStep 3 — Probability\nP = 15/105 = 1/7\n\nCorrect option: A) 1/7",
                "Accenture", 2022, "ASE Final Round"));

        qs.add(q("Hard",
                "If the difference between compound interest and simple interest on a sum at 10% for 2 years is ₹50, find the principal.",
                a("₹4000", "₹5000", "₹5500", "₹6000"), 1,
                "For 2 years: CI − SI = P × (r/100)².",
                "Rate = 10%, Time = 2 years, CI − SI = ₹50\n\nStep 1 — Use 2-year CI−SI formula\nCI − SI = P × (r/100)²\n50 = P × (10/100)²\n50 = P × 0.01\n\nStep 2 — Solve for P\nP = 50 / 0.01 = ₹5000\n\nCorrect option: B) ₹5000",
                "Tech Mahindra", 2023, "Associate Aptitude"));

        qs.add(q("Hard",
                "A can do a work in 18 days and B in 24 days. They work together for 6 days, then A leaves. How many more days will B take?",
                a("8", "10", "12", "14"), 1,
                "Find work done in 6 days together, subtract from 1, divide remainder by B's rate.",
                "A → 1/18 per day, B → 1/24 per day\n\nStep 1 — Work done in 6 days together\n6 × (1/18 + 1/24) = 6 × (4+3)/72 = 6 × 7/72 = 7/12\n\nStep 2 — Remaining work\n1 − 7/12 = 5/12\n\nStep 3 — Days for B alone\n(5/12) ÷ (1/24) = (5/12) × 24 = 10 days\n\nCorrect option: B) 10 days",
                "Amazon", 2024, "SDE OA Hard"));

        qs.add(q("Hard",
                "What is the least number that must be added to 1056 to make it exactly divisible by 23?",
                a("2", "3", "4", "5"), 0,
                "Divide 1056 by 23 and find how much is needed to reach the next multiple.",
                "Find remainder of 1056 ÷ 23\n\nStep 1 — Divide\n23 × 45 = 1035\nRemainder = 1056 − 1035 = 21\n\nStep 2 — Next multiple of 23\n23 × 46 = 1058\n\nStep 3 — Number to add\n1058 − 1056 = 2\n\nCorrect option: A) 2",
                "Deloitte", 2022, "Analyst Final Aptitude"));

        return qs;
    }

    private static Question q(String d, String t, String[] o, int ans, String h, String e,
                              String company, int year, String test) {
        return new Question(d, t, o, ans, h, e, company, year, test);
    }

    private static String[] a(String a, String b, String c, String d) { return new String[]{a, b, c, d}; }
}
