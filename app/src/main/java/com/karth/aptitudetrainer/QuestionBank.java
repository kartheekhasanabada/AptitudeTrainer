package com.karth.aptitudetrainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

public final class QuestionBank {
    private QuestionBank() {}

    public static List<Question> forDifficulty(String difficulty, int count) {
        return forDifficulty(difficulty, count, null);
    }

    public static List<Question> forDifficulty(String difficulty, int count, Set<String> skippedQuestionIds) {
        List<Question> filtered = new ArrayList<>();
        for (Question q : all()) {
            if (q.difficulty.equalsIgnoreCase(difficulty) && (skippedQuestionIds == null || !skippedQuestionIds.contains(q.stableId()))) {
                filtered.add(q);
            }
        }
        Collections.shuffle(filtered, new Random(System.nanoTime()));
        if (count < filtered.size()) return new ArrayList<>(filtered.subList(0, count));
        return filtered;
    }

    public static int countForDifficulty(String difficulty) {
        int c = 0;
        for (Question q : all()) if (q.difficulty.equalsIgnoreCase(difficulty)) c++;
        return c;
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
