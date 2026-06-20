package com.karth.aptitudetrainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class QuestionBank {
    private QuestionBank() {}

    public static List<Question> forDifficulty(String difficulty, int count) {
        List<Question> filtered = new ArrayList<>();
        for (Question q : all()) {
            if (q.difficulty.equalsIgnoreCase(difficulty)) filtered.add(q);
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
        qs.add(q("Easy", "A train running at 60 km/h crosses a pole in 9 seconds. What is the length of the train?", a("120 m", "150 m", "180 m", "200 m"), 1, "Convert km/h to m/s, then multiply by time.", "60 km/h = 60 × 5/18 = 16.67 m/s. Length = speed × time = 16.67 × 9 = 150 m."));
        qs.add(q("Easy", "If the cost price is ₹500 and profit is 20%, what is the selling price?", a("₹550", "₹580", "₹600", "₹620"), 2, "Selling price = CP × (100 + profit%)/100.", "SP = 500 × 120/100 = ₹600."));
        qs.add(q("Easy", "What is the average of 12, 18, 20, 25 and 30?", a("19", "20", "21", "22"), 2, "Add the numbers and divide by 5.", "Sum = 105. Average = 105/5 = 21."));
        qs.add(q("Easy", "A man walks 4 km north, then 3 km east. How far is he from the starting point?", a("5 km", "6 km", "7 km", "8 km"), 0, "Use Pythagoras theorem.", "Distance = √(4² + 3²) = √25 = 5 km."));
        qs.add(q("Easy", "Find the next number: 2, 4, 8, 16, ?", a("20", "24", "30", "32"), 3, "Each term is multiplied by the same number.", "The sequence doubles each time. 16 × 2 = 32."));
        qs.add(q("Easy", "If 15% of a number is 45, what is the number?", a("250", "280", "300", "350"), 2, "Number = part × 100 / percentage.", "Number = 45 × 100 / 15 = 300."));
        qs.add(q("Easy", "A can finish a work in 10 days. How much work does A finish in 1 day?", a("1/5", "1/8", "1/10", "1/12"), 2, "One-day work is reciprocal of total days.", "A's one-day work = 1/10 of the job."));
        qs.add(q("Easy", "The ratio of boys to girls is 3:2. If there are 30 boys, how many girls are there?", a("15", "18", "20", "25"), 2, "3 parts correspond to 30 boys.", "1 part = 10. Girls = 2 parts = 20."));
        qs.add(q("Easy", "What is the simple interest on ₹1000 at 5% per annum for 2 years?", a("₹50", "₹75", "₹100", "₹150"), 2, "SI = PRT/100.", "SI = 1000 × 5 × 2 / 100 = ₹100."));
        qs.add(q("Easy", "Find the odd one out: 9, 16, 25, 36, 45", a("16", "25", "36", "45"), 3, "Most numbers are perfect squares.", "9, 16, 25, and 36 are perfect squares. 45 is not."));

        // Medium
        qs.add(q("Medium", "Two numbers are in the ratio 5:7 and their sum is 144. Find the larger number.", a("60", "72", "84", "96"), 2, "Total parts = 12.", "12 parts = 144, so 1 part = 12. Larger = 7 × 12 = 84."));
        qs.add(q("Medium", "A sum becomes ₹7200 in 2 years at 20% compound interest compounded annually. Find the principal.", a("₹4800", "₹5000", "₹5200", "₹5500"), 1, "Amount = P(1 + r/100)^n.", "7200 = P × 1.2² = P × 1.44. P = 7200/1.44 = ₹5000."));
        qs.add(q("Medium", "A boat goes 30 km downstream in 2 hours and 20 km upstream in 2 hours. Find the speed of the stream.", a("2 km/h", "2.5 km/h", "3 km/h", "4 km/h"), 1, "Downstream speed = boat + stream; upstream = boat - stream.", "Downstream speed = 15 km/h, upstream = 10 km/h. Stream speed = (15 - 10)/2 = 2.5 km/h."));
        qs.add(q("Medium", "A shopkeeper marks an item 40% above cost and gives 10% discount. What is the profit percent?", a("24%", "25%", "26%", "30%"), 2, "Assume cost price = 100.", "Marked price = 140. Selling price after 10% discount = 126. Profit = 26%."));
        qs.add(q("Medium", "How many ways can the letters of the word CODE be arranged?", a("12", "16", "20", "24"), 3, "All letters are distinct.", "Number of arrangements = 4! = 24."));
        qs.add(q("Medium", "A and B can do a job in 12 days. A alone can do it in 20 days. How long will B alone take?", a("25 days", "30 days", "35 days", "40 days"), 1, "B's work rate = combined rate - A's rate.", "B rate = 1/12 - 1/20 = 2/60 = 1/30. So B takes 30 days."));
        qs.add(q("Medium", "Find the missing term: 3, 7, 15, 31, 63, ?", a("95", "111", "127", "135"), 2, "Each term is double the previous term plus 1.", "63 × 2 + 1 = 127."));
        qs.add(q("Medium", "A person sells two items at ₹990 each. On one he gains 10%, on the other he loses 10%. What is the overall result?", a("No profit no loss", "1% loss", "2% loss", "5% profit"), 1, "Equal selling prices with equal gain/loss percent cause a loss.", "Loss% = x²/100 = 10²/100 = 1% loss."));
        qs.add(q("Medium", "If x + 1/x = 5, find x² + 1/x².", a("21", "22", "23", "25"), 2, "Square both sides.", "(x + 1/x)² = x² + 2 + 1/x² = 25. Therefore x² + 1/x² = 23."));
        qs.add(q("Medium", "A jar contains 5 red and 7 blue balls. One ball is drawn randomly. What is the probability it is red?", a("5/7", "5/12", "7/12", "1/2"), 1, "Probability = favorable outcomes / total outcomes.", "Favorable red balls = 5, total balls = 12. Probability = 5/12."));

        // Hard
        qs.add(q("Hard", "In how many ways can 5 boys and 4 girls be seated in a row so that no two girls sit together?", a("2880", "4320", "8640", "17280"), 2, "Arrange boys first, then place girls in gaps.", "Arrange 5 boys in 5! ways. There are 6 gaps around boys; choose 4 for girls and arrange them: C(6,4) × 4!. Total = 120 × 15 × 24 = 43200? Wait options are smaller; if girls are identical it is 1800. For distinct girls, correct total is 43200. This item is removed in app logic by replacement."));
        qs.remove(qs.size()-1);
        qs.add(q("Hard", "In how many ways can 4 boys and 3 girls be seated in a row so that no two girls sit together?", a("720", "1440", "2880", "3600"), 1, "Arrange boys first, then use the gaps.", "Arrange 4 boys: 4! = 24. There are 5 gaps. Choose 3 gaps for girls: C(5,3) = 10. Arrange girls: 3! = 6. Total = 24 × 10 × 6 = 1440."));
        qs.add(q("Hard", "A train of length 200 m crosses a platform of length 300 m in 25 seconds. What is the train speed?", a("60 km/h", "72 km/h", "80 km/h", "90 km/h"), 1, "Total distance = train length + platform length.", "Distance = 500 m. Speed = 500/25 = 20 m/s = 20 × 18/5 = 72 km/h."));
        qs.add(q("Hard", "The average age of 30 students is 15 years. When the teacher's age is included, the average becomes 16. Find the teacher's age.", a("40", "42", "44", "46"), 3, "Compare total ages before and after including teacher.", "Total age of students = 30 × 15 = 450. Total with teacher = 31 × 16 = 496. Teacher age = 46."));
        qs.add(q("Hard", "A mixture contains milk and water in ratio 7:3. How much water must be added to 40 litres of mixture to make the ratio 7:5?", a("6 L", "8 L", "10 L", "12 L"), 1, "Milk quantity remains constant.", "In 40 L, milk = 28 L and water = 12 L. To make ratio 7:5, water should be 28 × 5/7 = 20 L. Add 8 L."));
        qs.add(q("Hard", "Three pipes A, B and C can fill a tank in 12, 15 and 20 hours. If all are opened together, how long will they take?", a("4 h", "5 h", "6 h", "8 h"), 1, "Add their one-hour work rates.", "Rate = 1/12 + 1/15 + 1/20 = 5/60 + 4/60 + 3/60 = 12/60 = 1/5. Time = 5 hours."));
        qs.add(q("Hard", "A number when divided by 5 leaves remainder 3. What remainder will its square leave when divided by 5?", a("1", "2", "3", "4"), 3, "Use modular arithmetic.", "If n ≡ 3 (mod 5), then n² ≡ 9 ≡ 4 (mod 5)."));
        qs.add(q("Hard", "A bag has 4 white, 5 black and 6 red balls. Two balls are drawn. What is probability both are red?", a("1/7", "2/7", "3/14", "1/5"), 0, "Use combinations or dependent probability.", "Probability = C(6,2)/C(15,2) = 15/105 = 1/7."));
        qs.add(q("Hard", "If the difference between compound interest and simple interest on a sum at 10% for 2 years is ₹50, find the principal.", a("₹4000", "₹5000", "₹5500", "₹6000"), 1, "For 2 years, CI - SI = P(r/100)^2.", "50 = P × (10/100)² = P/100. Therefore P = ₹5000."));
        qs.add(q("Hard", "A can do a work in 18 days and B in 24 days. They work together for 6 days, then A leaves. How many more days will B take?", a("8", "10", "12", "14"), 1, "Find work done together, then remaining work by B.", "Together in 6 days: 6(1/18 + 1/24)=6(7/72)=7/12. Remaining = 5/12. B does 1/24 per day, so time = (5/12)×24 = 10 days."));
        qs.add(q("Hard", "What is the least number that must be added to 1056 to make it exactly divisible by 23?", a("2", "3", "4", "5"), 2, "Find the remainder of 1056 divided by 23.", "23 × 45 = 1035, remainder = 21. Need 2 more to reach next multiple? 1058 is 23 × 46, so add 2. Correct option is 2."));
        // fix previous answer index after validating explanation
        qs.remove(qs.size()-1);
        qs.add(q("Hard", "What is the least number that must be added to 1056 to make it exactly divisible by 23?", a("2", "3", "4", "5"), 0, "Find the remainder of 1056 divided by 23.", "23 × 45 = 1035, remainder = 21. The next multiple is 23 × 46 = 1058, so add 2."));
        return qs;
    }

    private static Question q(String d, String t, String[] o, int ans, String h, String e) { return new Question(d, t, o, ans, h, e); }
    private static String[] a(String a, String b, String c, String d) { return new String[]{a,b,c,d}; }
}
