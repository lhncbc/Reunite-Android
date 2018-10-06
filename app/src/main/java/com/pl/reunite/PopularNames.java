/*
 * Informational Notice:
 *
 * This software, the ”TBD,” was developed under contract funded by the National Library of Medicine, which is part of the National Institutes of Health, an agency of the Department of Health and Human Services, United States Government.
 *
 * The license of this software is an open-source BSD license.  It allows use in both commercial and non-commercial products.
 *
 * The license does not supersede any applicable United States law.
 *
 * The license does not indemnify you from any claims brought by third parties whose proprietary rights may be infringed by your usage of this software.
 *
 * Government usage rights for this software are established by Federal law, which includes, but may not be limited to, Federal Acquisition Regulation (FAR) 48 C.F.R. Part52.227-14, Rights in Data—General.
 * The license for this software is intended to be expansive, rather than restrictive, in encouraging the use of this software in both commercial and non-commercial products.
 *
 * LICENSE:
 *
 * Government Usage Rights Notice:  The U.S. Government retains unlimited, royalty-free usage rights to this software, but not ownership, as provided by Federal law.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * •	Redistributions of source code must retain the above Government Usage Rights Notice, this list of conditions and the following disclaimer.
 *
 * •	Redistributions in binary form must reproduce the above Government Usage Rights Notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * •	The names,trademarks, and service marks of the National Library of Medicine, the National Cancer Institute, the National Institutes of Health, and the names of any of the software developers shall not be used to endorse or promote products derived from this software without specific prior written permission.
 */

package com.pl.reunite;

/**
 * Created on 9/4/2014.
 * This file is added in version 8.0.5 of TriagePic
 * 100 the most common last names
 * 100 the most common male first names
 * 100 the most common female first names
 * randomly to generated a person's name for test.
 */
public class PopularNames {
    private String languageCode;
    private String selBoyNames[];
    private String selGirlNames[];
    private String selLastNames[];
    public static boolean MALE = true;
    public static boolean FEMALE = false;
    public static int MAX_NAMES = 99;
    private boolean gender; // true for male and false for girl;
    public boolean getGender() {
        return this.gender;
    }
    public void setGender(boolean gender){
        this.gender = gender;
    }
    public String getRandomBoyName(){
        int index = getRandomNumber(0, selBoyNames.length);
        return selBoyNames[index];
    }
    public String getRandomGirlName(){
        int index = getRandomNumber(0, selGirlNames.length);
        return selGirlNames[index];
    }
    public String getRandomLastName() {
        int index = getRandomNumber(0, selLastNames.length);
        return selLastNames[index];
    }
    int getRandomNumber(int min, int max){

        return randomWithRange(min, max);
    }
    int randomWithRange(int min, int max)
    {
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;
    }

    PopularNames(){
        languageCode = "en";
        selBoyNames = boys;
        selGirlNames = girls;
        selLastNames = lastNames;
    }

    PopularNames(String languageCode){
        this.languageCode = languageCode;
        if (languageCode.equalsIgnoreCase("es") == true){
            selBoyNames = hispanic_boys;
            selGirlNames = hispanic_girls;
            selLastNames = hispanic_lastNames;
        }
        else if (languageCode.equalsIgnoreCase("zh") == true || languageCode.equalsIgnoreCase("cn") == true){
            selBoyNames = cn_boys;
            selGirlNames = cn_girls;
            selLastNames = cn_lastNames;
        }
        else if (languageCode.equalsIgnoreCase("tw") == true){
            selBoyNames = tw_boys;
            selGirlNames = tw_girls;
            selLastNames = tw_lastNames;
        }
        else {
            selBoyNames = boys;
            selGirlNames = girls;
            selLastNames = lastNames;
        }
    }

    private static String [] girls = {
            "Sophia",
            "Emma",
            "Olivia",
            "Isabella",
            "Mia",
            "Ava",
            "Lily",
            "Zoe",
            "Emily",
            "Chloe",
            "Layla",
            "Madison",
            "Madelyn",
            "Abigail",
            "Aubrey",
            "Charlotte",
            "Amelia",
            "Ella",
            "Kaylee",
            "Avery",
            "Aaliyah",
            "Hailey",
            "Hannah",
            "Addison",
            "Riley",
            "Harper",
            "Aria",
            "Arianna",
            "Mackenzie",
            "Lila",
            "Evelyn",
            "Adalyn",
            "Grace",
            "Brooklyn",
            "Ellie",
            "Anna",
            "Kaitlyn",
            "Isabelle",
            "Sophie",
            "Scarlett",
            "Natalie",
            "Leah",
            "Sarah",
            "Nora",
            "Mila",
            "Elizabeth",
            "Lillian",
            "Kylie",
            "Audrey",
            "Lucy",
            "Maya",
            "Annabelle",
            "Makayla",
            "Gabriella",
            "Elena",
            "Victoria",
            "Claire",
            "Savannah",
            "Peyton",
            "Maria",
            "Alaina",
            "Kennedy",
            "Stella",
            "Liliana",
            "Allison",
            "Samantha",
            "Keira",
            "Alyssa",
            "Reagan",
            "Molly",
            "Alexandra",
            "Violet",
            "Charlie",
            "Julia",
            "Sadie",
            "Ruby",
            "Eva",
            "Alice",
            "Eliana",
            "Taylor",
            "Callie",
            "Penelope",
            "Camilla",
            "Bailey",
            "Kaelyn",
            "Alexis",
            "Kayla",
            "Katherine",
            "Sydney",
            "Lauren",
            "Jasmine",
            "London",
            "Bella",
            "Adeline",
            "Caroline",
            "Vivian",
            "Juliana",
            "Gianna",
            "Skyler",
            "Jordyn"
    };
    private static String [] boys = {
            "Jackson",
            "Aiden",
            "Liam",
            "Lucas",
            "Noah",
            "Mason",
            "Jayden",
            "Ethan",
            "Jacob",
            "Jack",
            "Caden",
            "Logan",
            "Benjamin",
            "Michael",
            "Caleb",
            "Ryan",
            "Alexander",
            "Elijah",
            "James",
            "William",
            "Oliver",
            "Connor",
            "Matthew",
            "Daniel",
            "Luke",
            "Brayden",
            "Jayce",
            "Henry",
            "Carter",
            "Dylan",
            "Gabriel",
            "Joshua",
            "Nicholas",
            "Isaac",
            "Owen",
            "Nathan",
            "Grayson",
            "Eli",
            "Landon",
            "Andrew",
            "Max",
            "Samuel",
            "Gavin",
            "Wyatt",
            "Christian",
            "Hunter",
            "Cameron",
            "Evan",
            "Charlie",
            "David",
            "Sebastian",
            "Joseph",
            "Dominic",
            "Anthony",
            "Colton",
            "John",
            "Tyler",
            "Zachary",
            "Thomas",
            "Julian",
            "Levi",
            "Adam",
            "Isaiah",
            "Alex",
            "Aaron",
            "Parker",
            "Cooper",
            "Miles",
            "Chase",
            "Muhammad",
            "Christopher",
            "Blake",
            "Austin",
            "Jordan",
            "Leo",
            "Jonathan",
            "Adrian",
            "Colin",
            "Hudson",
            "Ian",
            "Xavier",
            "Camden",
            "Tristan",
            "Carson",
            "Jason",
            "Nolan",
            "Riley",
            "Lincoln",
            "Brody",
            "Bentley",
            "Nathaniel",
            "Josiah",
            "Declan",
            "Jake",
            "Asher",
            "Jeremiah",
            "Cole",
            "Mateo",
            "Micah",
            "Elliot"
    };
    private static String [] lastNames = {
            "Smith",
            "Johnson",
            "Williams",
            "Jones",
            "Brown",
            "Davis",
            "Miller",
            "Wilson",
            "Moore",
            "Taylor",
            "Anderson",
            "Thomas",
            "Jackson",
            "White",
            "Harris",
            "Martin",
            "Thompson",
            "Garcia",
            "Martinez",
            "Robinson",
            "Clark",
            "Rodriguez",
            "Lewis",
            "Lee",
            "Walker",
            "Hall",
            "Allen",
            "Young",
            "Hernandez",
            "King",
            "Wright",
            "Lopez",
            "Hill",
            "Scott",
            "Green",
            "Adams",
            "Baker",
            "Gonzalez",
            "Nelson",
            "Carter",
            "Mitchell",
            "Perez",
            "Roberts",
            "Turner",
            "Phillips",
            "Campbell",
            "Parker",
            "Evans",
            "Edwards",
            "Collins",
            "Stewart",
            "Sanchez",
            "Morris",
            "Rogers",
            "Reed",
            "Cook",
            "Morgan",
            "Bell",
            "Murphy",
            "Bailey",
            "Rivera",
            "Cooper",
            "Richardson",
            "Cox",
            "Howard",
            "Ward",
            "Torres",
            "Peterson",
            "Gray",
            "Ramirez",
            "James",
            "Watson",
            "Brooks",
            "Kelly",
            "Sanders",
            "Price",
            "Bennett",
            "Wood",
            "Barnes",
            "Ross",
            "Henderson",
            "Coleman",
            "Jenkins",
            "Perry",
            "Powell",
            "Long",
            "Patterson",
            "Hughes",
            "Flores",
            "Washington",
            "Butler",
            "Simmons",
            "Foster",
            "Gonzales",
            "Bryant",
            "Alexander",
            "Russell",
            "Griffin",
            "Diaz",
            "Hayes"
    };

    private static String [] hispanic_girls = {
            "Maria José",
            "Mía",
            "María Fernanda",
            "Salomé",
            "María Alejandra",
            "Sara Sofía",
            "María Camila"
    };

    private static String [] hispanic_boys = {
            "Sebastián",
            "Matías",
            "Nicolás",
            "Benjamín",
            "Joaquín",
            "Jerónimo",
            "Agustín",
            "Juan José",
            "Andrés",
            "Ángel",
            "Adrián",
            "Miguel Ángel",
            "Julián",
            "Juan Sebastián",
            "Aarón",
            "Máximo",
            "Cristóbal",
            "Simón",
            "Josué",
            "Damián",
            "Juan Martín",
            "Álvaro",
            "Valentín",
            "Jesús",
            "Elías"
    };

    private static String [] hispanic_lastNames = {
            "Abadía",
            "Albarracín",
            "Alcalá",
            "Alegría",
            "Alén",
            "Álvarez",
            "Aragonés",
            "Asín",
            "Ávila",
            "Avilés",
            "Álvarez",
            "Côté",
            "Gagné",
            "Rodríguez",
            "Hernandez",
            "García",
            "Martínez",
            "González",
            "López",
            "Pérez",
            "Sánchez",
            "Ramírez",
            "Rodríguez",
            "Pérez",
            "González",
            "Hernández",
            "Fernández",
            "López"
    };

    private static String [] cn_boys = {
            "辰逸",
            "皓轩",
            "瑾瑜",
            "擎苍",
            "擎宇",
            "致远",
            "烨磊",
            "英杰",
            "文博",
            "晟睿",
            "俊驰",
            "雨泽",
            "文昊",
            "烨磊",
            "擎苍",
            "浩宇",
            "志泽",
            "修洁",
            "天佑",
            "俊楠"
    };

    private static String [] cn_girls = {
            "娟",
            "娉",
            "婷",
            "妮",
            "娜",
            "菲儿",
            "菁菁",
            "彤彤",
            "娇儿",
            "贤淑",
            "安静",
            "雨泽",
            "慧娴",
            "智敏",
            "菲儿",
            "浩宇",
            "文雅",
            "淑贤",
            "文清",
            "婉贞"
    };

    private static String [] cn_lastNames = {
            "王",
            "李",
            "张",
            "刘",
            "孙",
            "陈",
            "杨",
            "黄",
            "吴",
            "赵",
            "周",
            "谢",
            "胡",
            "唐",
            "林",
            "徐",
            "姚",
            "葛",
            "马",
            "万",
            "朱",
            "何",
            "郭",
            "高",
            "罗"
    };
    private static String [] tw_boys = {
            "辰逸",
            "皓軒",
            "瑾瑜",
            "擎蒼",
            "擎宇",
            "緻遠",
            "燁磊",
            "英傑",
            "文博",
            "晟睿",
            "俊馳",
            "雨澤",
            "文昊",
            "燁磊",
            "擎蒼",
            "浩宇",
            "誌澤",
            "修潔",
            "天佑",
            "俊楠"
    };

    private static String [] tw_girls = {
            "娟",
            "娉",
            "婷",
            "妮",
            "娜",
            "菲兒",
            "菁菁",
            "彤彤",
            "嬌兒",
            "賢淑",
            "安靜",
            "雨澤",
            "慧嫻",
            "智敏",
            "菲兒",
            "浩宇",
            "文雅",
            "淑賢",
            "文清",
            "婉貞"
    };

    private static String [] tw_lastNames = {
            "王",
            "李",
            "張",
            "劉",
            "孫",
            "陳",
            "楊",
            "黃",
            "吳",
            "趙",
            "周",
            "謝",
            "衚",
            "唐",
            "林",
            "徐",
            "姚",
            "葛",
            "馬",
            "萬",
            "朱",
            "何",
            "郭",
            "高",
            "羅"
    };
}
