package com.earthapp.account

/**
 * Represents a country in the Earth App.
 */
enum class Country {

    /**
     * Represents an international region. Serves as a placeholder for countries not listed or when the user has not specified one.
     */
    INTERNATIONAL("", "International", "en-INT", "🌐", "+0"),

    /**
     * Represents the country of the United States.
     */
    UNITED_STATES("US", "United States", "en-US", "🇺🇸", "+1"),

    /**
     * Represents the country of Afghanistan.
     */
    AFGHANISTAN("AF", "Afghanistan", "fa-AF", "🇦🇫", "+93"),

    /**
     * Represents the country of Albania.
     */
    ALBANIA("AL", "Albania", "sq-AL", "🇦🇱", "+355"),

    /**
     * Represents the country of Algeria.
     */
    ALGERIA("DZ", "Algeria", "ar-DZ", "🇩🇿", "+213"),

    /**
     * Represents the country of Andorra.
     */
    ANDORRA("AD", "Andorra", "ca-AD", "🇦🇩", "+376"),

    /**
     * Represents the country of Angola.
     */
    ANGOLA("AO", "Angola", "pt-AO", "🇦🇴", "+244"),

    /**
     * Represents the country of Antigua and Barbuda.
     */
    ANTIGUA_AND_BARBUDA("AG", "Antigua and Barbuda", "en-AG", "🇦🇬", "+1"),

    /**
     * Represents the country of Argentina.
     */
    ARGENTINA("AR", "Argentina", "es-AR", "🇦🇷", "+54"),

    /**
     * Represents the country of Armenia.
     */
    ARMENIA("AM", "Armenia", "hy-AM", "🇦🇲", "+374"),

    /**
     * Represents the country of Australia.
     */
    AUSTRALIA("AU", "Australia", "en-AU", "🇦🇺", "+61"),

    /**
     * Represents the country of Austria.
     */
    AUSTRIA("AT", "Austria", "de-AT", "🇦🇹", "+43"),

    /**
     * Represents the country of Azerbaijan.
     */
    AZERBAIJAN("AZ", "Azerbaijan", "az-AZ", "🇦🇿", "+994"),

    /**
     * Represents the country of the Bahamas.
     */
    BAHAMAS("BS", "Bahamas", "en-BS", "🇧🇸", "+1"),

    /**
     * Represents the country of Bahrain.
     */
    BAHRAIN("BH", "Bahrain", "ar-BH", "🇧🇭", "+973"),

    /**
     * Represents the country of Bangladesh.
     */
    BANGLADESH("BD", "Bangladesh", "bn-BD", "🇧🇩", "+880"),

    /**
     * Represents the country of Barbados.
     */
    BARBADOS("BB", "Barbados", "en-BB", "🇧🇧", "+1"),

    /**
     * Represents the country of Belarus.
     */
    BELARUS("BY", "Belarus", "be-BY", "🇧🇾", "+375"),

    /**
     * Represents the country of Belgium.
     */
    BELGIUM("BE", "Belgium", "nl-BE", "🇧🇪", "+32"),

    /**
     * Represents the country of Belize.
     */
    BELIZE("BZ", "Belize", "en-BZ", "🇧🇿", "+501"),

    /**
     * Represents the country of Benin.
     */
    BENIN("BJ", "Benin", "fr-BJ", "🇧🇯", "+229"),

    /**
     * Represents the country of Bhutan.
     */
    BHUTAN("BT", "Bhutan", "dz-BT", "🇧🇹", "+975"),

    /**
     * Represents the country of Bolivia.
     */
    BOLIVIA("BO", "Bolivia", "es-BO", "🇧🇴", "+591"),

    /**
     * Represents the country of Bosnia and Herzegovina.
     */
    BOSNIA_AND_HERZEGOVINA("BA", "Bosnia and Herzegovina", "bs-BA", "🇧🇦", "+387"),

    /**
     * Represents the country of Botswana.
     */
    BOTSWANA("BW", "Botswana", "en-BW", "🇧🇼", "+267"),

    /**
     * Represents the country of Brazil.
     */
    BRAZIL("BR", "Brazil", "pt-BR", "🇧🇷", "+55"),

    /**
     * Represents the country of Brunei.
     */
    BRUNEI("BN", "Brunei", "ms-BN", "🇧🇳", "+673"),

    /**
     * Represents the country of Bulgaria.
     */
    BULGARIA("BG", "Bulgaria", "bg-BG", "🇧🇬", "+359"),

    /**
     * Represents the country of Burkina Faso.
     */
    BURKINA_FASO("BF", "Burkina Faso", "fr-BF", "🇧🇫", "+226"),

    /**
     * Represents the country of Burundi.
     */
    BURUNDI("BI", "Burundi", "rn-BI", "🇧🇮", "+257"),

    /**
     * Represents the country of Cabo Verde.
     */
    CABO_VERDE("CV", "Cabo Verde", "pt-CV", "🇨🇻", "+238"),

    /**
     * Represents the country of Cambodia.
     */
    CAMBODIA("KH", "Cambodia", "km-KH", "🇰🇭", "+855"),

    /**
     * Represents the country of Cameroon.
     */
    CAMEROON("CM", "Cameroon", "fr-CM", "🇨🇲", "+237"),

    /**
     * Represents the country of Canada.
     */
    CANADA("CA", "Canada", "en-CA", "🇨🇦", "+1"),

    /**
     * Represents the country of the Central African Republic.
     */
    CENTRAL_AFRICAN_REPUBLIC("CF", "Central African Republic", "fr-CF", "🇨🇫", "+236"),

    /**
     * Represents the country of Chad.
     */
    CHAD("TD", "Chad", "fr-TD", "🇹🇩", "+235"),

    /**
     * Represents the country of Chile.
     */
    CHILE("CL", "Chile", "es-CL", "🇨🇱", "+56"),

    /**
     * Represents the country of China.
     */
    CHINA("CN", "China", "zh-CN", "🇨🇳", "+86"),

    /**
     * Represents the country of Colombia.
     */
    COLOMBIA("CO", "Colombia", "es-CO", "🇨🇴", "+57"),

    /**
     * Represents the country of the Comoros.
     */
    COMOROS("KM", "Comoros", "ar-KM", "🇰🇲", "+269"),

    /**
     * Represents the country of Congo.
     */
    CONGO("CG", "Congo", "fr-CG", "🇨🇬", "+242"),

    /**
     * Represents the country of Costa Rica.
     */
    COSTA_RICA("CR", "Costa Rica", "es-CR", "🇨🇷", "+506"),

    /**
     * Represents the country of Croatia.
     */
    CROATIA("HR", "Croatia", "hr-HR", "🇭🇷", "+385"),

    /**
     * Represents the country of Cuba.
     */
    CUBA("CU", "Cuba", "es-CU", "🇨🇺", "+53"),

    /**
     * Represents the country of Cyprus.
     */
    CYPRUS("CY", "Cyprus", "el-CY", "🇨🇾", "+357"),

    /**
     * Represents the country of the Czech Republic.
     */
    CZECH_REPUBLIC("CZ", "Czech Republic", "cs-CZ", "🇨🇿", "+420"),

    /**
     * Represents the country of Democratic Republic of the Congo.
     */
    DEMOCRATIC_REPUBLIC_OF_THE_CONGO("CD", "Democratic Republic of the Congo", "fr-CD", "🇨🇩", "+243"),

    /**
     * Represents the country of Denmark.
     */
    DENMARK("DK", "Denmark", "da-DK", "🇩🇰", "+45"),

    /**
     * Represents the country of Djibouti.
     */
    DJIBOUTI("DJ", "Djibouti", "fr-DJ", "🇩🇯", "+253"),

    /**
     * Represents the country of Dominica.
     */
    DOMINICA("DM", "Dominica", "en-DM", "🇩🇲", "+1"),

    /**
     * Represents the country of the Dominican Republic.
     */
    DOMINICAN_REPUBLIC("DO", "Dominican Republic", "es-DO", "🇩🇴", "+1"),

    /**
     * Represents the country of Ecuador.
     */
    ECUADOR("EC", "Ecuador", "es-EC", "🇪🇨", "+593"),

    /**
     * Represents the country of Egypt.
     */
    EGYPT("EG", "Egypt", "ar-EG", "🇪🇬", "+20"),

    /**
     * Represents the country of El Salvador.
     */
    EL_SALVADOR("SV", "El Salvador", "es-SV", "🇸🇻", "+503"),

    /**
     * Represents the country of Equatorial Guinea.
     */
    EQUATORIAL_GUINEA("GQ", "Equatorial Guinea", "es-GQ", "🇬🇶", "+240"),

    /**
     * Represents the country of Eritrea.
     */
    ERITREA("ER", "Eritrea", "ti-ER", "🇪🇷", "+291"),

    /**
     * Represents the country of Estonia.
     */
    ESTONIA("EE", "Estonia", "et-EE", "🇪🇪", "+372"),

    /**
     * Represents the country of Eswatini.
     */
    ESWATINI("SZ", "Eswatini", "en-SZ", "🇸🇿", "+268"),

    /**
     * Represents the country of Ethiopia.
     */
    ETHIOPIA("ET", "Ethiopia", "am-ET", "🇪🇹", "+251"),

    /**
     * Represents the country of Fiji.
     */
    FIJI("FJ", "Fiji", "en-FJ", "🇫🇯", "+679"),

    /**
     * Represents the country of Finland.
     */
    FINLAND("FI", "Finland", "fi-FI", "🇫🇮", "+358"),

    /**
     * Represents the country of France.
     */
    FRANCE("FR", "France", "fr-FR", "🇫🇷", "+33"),

    /**
     * Represents the country of Gabon.
     */
    GABON("GA", "Gabon", "fr-GA", "🇬🇦", "+241"),

    /**
     * Represents the country of the Gambia.
     */
    GAMBIA("GM", "Gambia", "en-GM", "🇬🇲", "+220"),

    /**
     * Represents the country of Georgia.
     */
    GEORGIA("GE", "Georgia", "ka-GE", "🇬🇪", "+995"),

    /**
     * Represents the country of Germany.
     */
    GERMANY("DE", "Germany", "de-DE", "🇩🇪", "+49"),

    /**
     * Represents the country of Ghana.
     */
    GHANA("GH", "Ghana", "en-GH", "🇬🇭", "+233"),

    /**
     * Represents the country of Greece.
     */
    GREECE("GR", "Greece", "el-GR", "🇬🇷", "+30"),

    /**
     * Represents the country of Grenada.
     */
    GRENADA("GD", "Grenada", "en-GD", "🇬🇩", "+1"),

    /**
     * Represents the country of Guatemala.
     */
    GUATEMALA("GT", "Guatemala", "es-GT", "🇬🇹", "+502"),

    /**
     * Represents the country of Guinea.
     */
    GUINEA("GN", "Guinea", "fr-GN", "🇬🇳", "+224"),

    /**
     * Represents the country of Guinea-Bissau.
     */
    GUINEA_BISSAU("GW", "Guinea-Bissau", "pt-GW", "🇬🇼", "+245"),

    /**
     * Represents the country of Guyana.
     */
    GUYANA("GY", "Guyana", "en-GY", "🇬🇾", "+592"),

    /**
     * Represents the country of Haiti.
     */
    HAITI("HT", "Haiti", "ht-HT", "🇭🇹", "+509"),

    /**
     * Represents the country of Honduras.
     */
    HONDURAS("HN", "Honduras", "es-HN", "🇭🇳", "+504"),

    /**
     * Represents the country of Hungary.
     */
    HUNGARY("HU", "Hungary", "hu-HU", "🇭🇺", "+36"),

    /**
     * Represents the country of Iceland.
     */
    ICELAND("IS", "Iceland", "is-IS", "🇮🇸", "+354"),

    /**
     * Represents the country of India.
     */
    INDIA("IN", "India", "hi-IN", "🇮🇳", "+91"),

    /**
     * Represents the country of Indonesia.
     */
    INDONESIA("ID", "Indonesia", "id-ID", "🇮🇩", "+62"),

    /**
     * Represents the country of Iran.
     */
    IRAN("IR", "Iran", "fa-IR", "🇮🇷", "+98"),

    /**
     * Represents the country of Iraq.
     */
    IRAQ("IQ", "Iraq", "ar-IQ", "🇮🇶", "+964"),

    /**
     * Represents the country of Ireland.
     */
    IRELAND("IE", "Ireland", "en-IE", "🇮🇪", "+353"),

    /**
     * Represents the country of Israel.
     */
    ISRAEL("IL", "Israel", "he-IL", "🇮🇱", "+972"),

    /**
     * Represents the country of Italy.
     */
    ITALY("IT", "Italy", "it-IT", "🇮🇹", "+39"),

    /**
     * Represents the country of the Ivory Coast.
     */
    IVORY_COAST("CI", "Ivory Coast", "fr-CI", "🇨🇮", "+225"),

    /**
     * Represents the country of Jamaica.
     */
    JAMAICA("JM", "Jamaica", "en-JM", "🇯🇲", "+1"),

    /**
     * Represents the country of Japan.
     */
    JAPAN("JP", "Japan", "ja-JP", "🇯🇵", "+81"),

    /**
     * Represents the country of Jordan.
     */
    JORDAN("JO", "Jordan", "ar-JO", "🇯🇴", "+962"),

    /**
     * Represents the country of Kazakhstan.
     */
    KAZAKHSTAN("KZ", "Kazakhstan", "kk-KZ", "🇰🇿", "+7"),

    /**
     * Represents the country of Kenya.
     */
    KENYA("KE", "Kenya", "sw-KE", "🇰🇪", "+254"),

    /**
     * Represents the country of Kiribati.
     */
    KIRIBATI("KI", "Kiribati", "en-KI", "🇰🇮", "+686"),

    /**
     * Represents the country of Kuwait.
     */
    KUWAIT("KW", "Kuwait", "ar-KW", "🇰🇼", "+965"),

    /**
     * Represents the country of Kyrgyzstan.
     */
    KYRGYZSTAN("KG", "Kyrgyzstan", "ky-KG", "🇰🇬", "+996"),

    /**
     * Represents the country of Laos.
     */
    LAOS("LA", "Laos", "lo-LA", "🇱🇦", "+856"),

    /**
     * Represents the country of Latvia.
     */
    LATVIA("LV", "Latvia", "lv-LV", "🇱🇻", "+371"),

    /**
     * Represents the country of Lebanon.
     */
    LEBANON("LB", "Lebanon", "ar-LB", "🇱🇧", "+961"),

    /**
     * Represents the country of Lesotho.
     */
    LESOTHO("LS", "Lesotho", "st-LS", "🇱🇸", "+266"),

    /**
     * Represents the country of Liberia.
     */
    LIBERIA("LR", "Liberia", "en-LR", "🇱🇷", "+231"),

    /**
     * Represents the country of Libya.
     */
    LIBYA("LY", "Libya", "ar-LY", "🇱🇾", "+218"),

    /**
     * Represents the country of Liechtenstein.
     */
    LIECHTENSTEIN("LI", "Liechtenstein", "de-LI", "🇱🇮", "+423"),

    /**
     * Represents the country of Lithuania.
     */
    LITHUANIA("LT", "Lithuania", "lt-LT", "🇱🇹", "+370"),

    /**
     * Represents the country of Luxembourg.
     */
    LUXEMBOURG("LU", "Luxembourg", "lb-LU", "🇱🇺", "+352"),

    /**
     * Represents the country of Madagascar.
     */
    MADAGASCAR("MG", "Madagascar", "mg-MG", "🇲🇬", "+261"),

    /**
     * Represents the country of Malawi.
     */
    MALAWI("MW", "Malawi", "en-MW", "🇲🇼", "+265"),

    /**
     * Represents the country of Malaysia.
     */
    MALAYSIA("MY", "Malaysia", "ms-MY", "🇲🇾", "+60"),

    /**
     * Represents the country of the Maldives.
     */
    MALDIVES("MV", "Maldives", "dv-MV", "🇲🇻", "+960"),

    /**
     * Represents the country of Mali.
     */
    MALI("ML", "Mali", "fr-ML", "🇲🇱", "+223"),

    /**
     * Represents the country of Malta.
     */
    MALTA("MT", "Malta", "mt-MT", "🇲🇹", "+356"),

    /**
     * Represents the country of the Marshall Islands.
     */
    MARSHALL_ISLANDS("MH", "Marshall Islands", "mh-MH", "🇲🇭", "+692"),

    /**
     * Represents the country of Mauritania.
     */
    MAURITANIA("MR", "Mauritania", "ar-MR", "🇲🇷", "+222"),

    /**
     * Represents the country of Mauritius.
     */
    MAURITIUS("MU", "Mauritius", "en-MU", "🇲🇺", "+230"),

    /**
     * Represents the country of Mexico.
     */
    MEXICO("MX", "Mexico", "es-MX", "🇲🇽", "+52"),

    /**
     * Represents the country of Micronesia.
     */
    MICRONESIA("FM", "Micronesia", "en-FM", "🇫🇲", "+691"),

    /**
     * Represents the country of Moldova.
     */
    MOLDOVA("MD", "Moldova", "ro-MD", "🇲🇩", "+373"),

    /**
     * Represents the country of Monaco.
     */
    MONACO("MC", "Monaco", "fr-MC", "🇲🇨", "+377"),

    /**
     * Represents the country of Mongolia.
     */
    MONGOLIA("MN", "Mongolia", "mn-MN", "🇲🇳", "+976"),

    /**
     * Represents the country of Montenegro.
     */
    MONTENEGRO("ME", "Montenegro", "sr-ME", "🇲🇪", "+382"),

    /**
     * Represents the country of Morocco.
     */
    MOROCCO("MA", "Morocco", "ar-MA", "🇲🇦", "+212"),

    /**
     * Represents the country of Mozambique.
     */
    MOZAMBIQUE("MZ", "Mozambique", "pt-MZ", "🇲🇿", "+258"),

    /**
     * Represents the country of Myanmar.
     */
    MYANMAR("MM", "Myanmar", "my-MM", "🇲🇲", "+95"),

    /**
     * Represents the country of Namibia.
     */
    NAMIBIA("NA", "Namibia", "en-NA", "🇳🇦", "+264"),

    /**
     * Represents the country of Nauru.
     */
    NAURU("NR", "Nauru", "na-NR", "🇳🇷", "+674"),

    /**
     * Represents the country of Nepal.
     */
    NEPAL("NP", "Nepal", "ne-NP", "🇳🇵", "+977"),

    /**
     * Represents the country of the Netherlands.
     */
    NETHERLANDS("NL", "Netherlands", "nl-NL", "🇳🇱", "+31"),

    /**
     * Represents the country of New Zealand.
     */
    NEW_ZEALAND("NZ", "New Zealand", "en-NZ", "🇳🇿", "+64"),

    /**
     * Represents the country of Nicaragua.
     */
    NICARAGUA("NI", "Nicaragua", "es-NI", "🇳🇮", "+505"),

    /**
     * Represents the country of Niger.
     */
    NIGER("NE", "Niger", "fr-NE", "🇳🇪", "+227"),

    /**
     * Represents the country of Nigeria.
     */
    NIGERIA("NG", "Nigeria", "en-NG", "🇳🇬", "+234"),

    /**
     * Represents the country of North Macedonia.
     */
    NORTH_MACEDONIA("MK", "North Macedonia", "mk-MK", "🇲🇰", "+389"),

    /**
     * Represents the country of Norway.
     */
    NORWAY("NO", "Norway", "no-NO", "🇳🇴", "+47"),

    /**
     * Represents the country of Oman.
     */
    OMAN("OM", "Oman", "ar-OM", "🇴🇲", "+968"),

    /**
     * Represents the country of Pakistan.
     */
    PAKISTAN("PK", "Pakistan", "ur-PK", "🇵🇰", "+92"),

    /**
     * Represents the country of Palau.
     */
    PALAU("PW", "Palau", "pau-PW", "🇵🇼", "+680"),

    /**
     * Represents the country of Panama.
     */
    PANAMA("PA", "Panama", "es-PA", "🇵🇦", "+507"),

    /**
     * Represents the country of Papua New Guinea.
     */
    PAPUA_NEW_GUINEA("PG", "Papua New Guinea", "en-PG", "🇵🇬", "+675"),

    /**
     * Represents the country of Paraguay.
     */
    PARAGUAY("PY", "Paraguay", "es-PY", "🇵🇾", "+595"),

    /**
     * Represents the country of Peru.
     */
    PERU("PE", "Peru", "es-PE", "🇵🇪", "+51"),

    /**
     * Represents the country of the Philippines.
     */
    PHILIPPINES("PH", "Philippines", "fil-PH", "🇵🇭", "+63"),

    /**
     * Represents the country of Poland.
     */
    POLAND("PL", "Poland", "pl-PL", "🇵🇱", "+48"),

    /**
     * Represents the country of Portugal.
     */
    PORTUGAL("PT", "Portugal", "pt-PT", "🇵🇹", "+351"),

    /**
     * Represents the country of Qatar.
     */
    QATAR("QA", "Qatar", "ar-QA", "🇶🇦", "+974"),

    /**
     * Represents the country of Romania.
     */
    ROMANIA("RO", "Romania", "ro-RO", "🇷🇴", "+40"),

    /**
     * Represents the country of Russia.
     */
    RUSSIA("RU", "Russia", "ru-RU", "🇷🇺", "+7"),

    /**
     * Represents the country of Rwanda.
     */
    RWANDA("RW", "Rwanda", "rw-RW", "🇷🇼", "+250"),

    /**
     * Represents the country of Saint Kitts and Nevis.
     */
    SAINT_KITTS_AND_NEVIS("KN", "Saint Kitts and Nevis", "en-KN", "🇰🇳", "+1"),

    /**
     * Represents the country of Saint Lucia.
     */
    SAINT_LUCIA("LC", "Saint Lucia", "en-LC", "🇱🇨", "+1"),

    /**
     * Represents the country of Saint Vincent and the Grenadines.
     */
    SAINT_VINCENT_AND_THE_GRENADINES("VC", "Saint Vincent and the Grenadines", "en-VC", "🇻🇨", "+1"),

    /**
     * Represents the country of Samoa.
     */
    SAMOA("WS", "Samoa", "sm-WS", "🇼🇸", "+685"),

    /**
     * Represents the country of San Marino.
     */
    SAN_MARINO("SM", "San Marino", "it-SM", "🇸🇲", "+378"),

    /**
     * Represents the country of São Tomé and Príncipe.
     */
    SAO_TOME_AND_PRINCIPE("ST", "Sao Tome and Principe", "pt-ST", "🇸🇹", "+239"),

    /**
     * Represents the country of Saudi Arabia.
     */
    SAUDI_ARABIA("SA", "Saudi Arabia", "ar-SA", "🇸🇦", "+966"),

    /**
     * Represents the country of Senegal.
     */
    SENEGAL("SN", "Senegal", "fr-SN", "🇸🇳", "+221"),

    /**
     * Represents the country of Serbia.
     */
    SERBIA("RS", "Serbia", "sr-RS", "🇷🇸", "+381"),

    /**
     * Represents the country of Seychelles.
     */
    SEYCHELLES("SC", "Seychelles", "en-SC", "🇸🇨", "+248"),

    /**
     * Represents the country of Sierra Leone.
     */
    SIERRA_LEONE("SL", "Sierra Leone", "en-SL", "🇸🇱", "+232"),

    /**
     * Represents the country of Singapore.
     */
    SINGAPORE("SG", "Singapore", "en-SG", "🇸🇬", "+65"),

    /**
     * Represents the country of Slovakia.
     */
    SLOVAKIA("SK", "Slovakia", "sk-SK", "🇸🇰", "+421"),

    /**
     * Represents the country of Slovenia.
     */
    SLOVENIA("SI", "Slovenia", "sl-SI", "🇸🇮", "+386"),

    /**
     * Represents the country of the Solomon Islands.
     */
    SOLOMON_ISLANDS("SB", "Solomon Islands", "en-SB", "🇸🇧", "+677"),

    /**
     * Represents the country of Somalia.
     */
    SOMALIA("SO", "Somalia", "so-SO", "🇸🇴", "+252"),

    /**
     * Represents the country of South Africa.
     */
    SOUTH_AFRICA("ZA", "South Africa", "af-ZA", "🇿🇦", "+27"),

    /**
     * Represents the country of South Korea.
     */
    SOUTH_KOREA("KR", "South Korea", "ko-KR", "🇰🇷", "+82"),

    /**
     * Represents the country of South Sudan.
     */
    SOUTH_SUDAN("SS", "South Sudan", "en-SS", "🇸🇸", "+211"),

    /**
     * Represents the country of Spain.
     */
    SPAIN("ES", "Spain", "es-ES", "🇪🇸", "+34"),

    /**
     * Represents the country of Sri Lanka.
     */
    SRI_LANKA("LK", "Sri Lanka", "si-LK", "🇱🇰", "+94"),

    /**
     * Represents the country of Sudan.
     */
    SUDAN("SD", "Sudan", "ar-SD", "🇸🇩", "+249"),

    /**
     * Represents the country of Suriname.
     */
    SURINAME("SR", "Suriname", "nl-SR", "🇸🇷", "+597"),

    /**
     * Represents the country of Sweden.
     */
    SWEDEN("SE", "Sweden", "sv-SE", "🇸🇪", "+46"),

    /**
     * Represents the country of Switzerland.
     */
    SWITZERLAND("CH", "Switzerland", "de-CH", "🇨🇭", "+41"),

    /**
     * Represents the country of Syria.
     */
    SYRIA("SY", "Syria", "ar-SY", "🇸🇾", "+963"),

    /**
     * Represents the country of Taiwan.
     */
    TAIWAN("TW", "Taiwan", "zh-TW", "🇹🇼", "+886"),

    /**
     * Represents the country of Tajikistan.
     */
    TAJIKISTAN("TJ", "Tajikistan", "tg-TJ", "🇹🇯", "+992"),

    /**
     * Represents the country of Tanzania.
     */
    TANZANIA("TZ", "Tanzania", "sw-TZ", "🇹🇿", "+255"),

    /**
     * Represents the country of Thailand.
     */
    THAILAND("TH", "Thailand", "th-TH", "🇹🇭", "+66"),

    /**
     * Represents the country of Timor-Leste.
     */
    TIMOR_LESTE("TL", "Timor-Leste", "pt-TL", "🇹🇱", "+670"),

    /**
     * Represents the country of Togo.
     */
    TOGO("TG", "Togo", "fr-TG", "🇹🇬", "+228"),

    /**
     * Represents the country of Tonga.
     */
    TONGA("TO", "Tonga", "to-TO", "🇹🇴", "+676"),

    /**
     * Represents the country of Trinidad and Tobago.
     */
    TRINIDAD_AND_TOBAGO("TT", "Trinidad and Tobago", "en-TT", "🇹🇹", "+1"),

    /**
     * Represents the country of Tunisia.
     */
    TUNISIA("TN", "Tunisia", "ar-TN", "🇹🇳", "+216"),

    /**
     * Represents the country of Turkey.
     */
    TURKEY("TR", "Turkey", "tr-TR", "🇹🇷", "+90"),

    /**
     * Represents the country of Turkmenistan.
     */
    TURKMENISTAN("TM", "Turkmenistan", "tk-TM", "🇹🇲", "+993"),

    /**
     * Represents the country of Tuvalu.
     */
    TUVALU("TV", "Tuvalu", "tvl-TV", "🇹🇻", "+688"),

    /**
     * Represents the country of Uganda.
     */
    UGANDA("UG", "Uganda", "en-UG", "🇺🇬", "+256"),

    /**
     * Represents the country of Ukraine.
     */
    UKRAINE("UA", "Ukraine", "uk-UA", "🇺🇦", "+380"),

    /**
     * Represents the country of the United Arab Emirates.
     */
    UNITED_ARAB_EMIRATES("AE", "United Arab Emirates", "ar-AE", "🇦🇪", "+971"),

    /**
     * Represents the country of the United Kingdom.
     */
    UNITED_KINGDOM("GB", "United Kingdom", "en-GB", "🇬🇧", "+44"),

    /**
     * Represents the country of Uruguay.
     */
    URUGUAY("UY", "Uruguay", "es-UY", "🇺🇾", "+598"),

    /**
     * Represents the country of Uzbekistan.
     */
    UZBEKISTAN("UZ", "Uzbekistan", "uz-UZ", "🇺🇿", "+998"),

    /**
     * Represents the country of Vanuatu.
     */
    VANUATU("VU", "Vanuatu", "bi-VU", "🇻🇺", "+678"),

    /**
     * Represents the country of Vatican City.
     */
    VATICAN_CITY("VA", "Vatican City", "it-VA", "🇻🇦", "+379"),

    /**
     * Represents the country of Venezuela.
     */
    VENEZUELA("VE", "Venezuela", "es-VE", "🇻🇪", "+58"),

    /**
     * Represents the country of Vietnam.
     */
    VIETNAM("VN", "Vietnam", "vi-VN", "🇻🇳", "+84"),

    /**
     * Represents the country of Yemen.
     */
    YEMEN("YE", "Yemen", "ar-YE", "🇾🇪", "+967"),

    /**
     * Represents the country of Zambia.
     */
    ZAMBIA("ZM", "Zambia", "en-ZM", "🇿🇲", "+260"),

    /**
     * Represents the country of Zimbabwe.
     */
    ZIMBABWE("ZW", "Zimbabwe", "en-ZW", "🇿🇼", "+263"),

    ;

    private val code: String
    private val countryName: String
    private val locale: String
    private val flagEmoji: String
    private val phonePrefix: String

    constructor(code: String, name: String, locale: String, flagEmoji: String, phonePrefix: String) {
        this.code = code
        this.countryName = name
        this.locale = locale
        this.flagEmoji = flagEmoji
        this.phonePrefix = phonePrefix
    }

    /**
     * Returns the code of the country.
     */
    fun getCode(): String = code

    /**
     * Returns the name of the country.
     */
    fun getCountryName(): String = countryName

    /**
     * Returns the locale of the country.
     */
    fun getLocale(): String = locale

    /**
     * Returns the flag emoji of the country.
     */
    fun getFlagEmoji(): String = flagEmoji

    /**
     * Returns the phone prefix of the country.
     */
    fun getPhonePrefix(): String = phonePrefix

    companion object {

        /**
         * Gets a country by its two-letter code.
         * @param code The country code to search for.
         * @return The country with the specified code, or null if not found.
         */
        fun fromCode(code: String): Country? = entries.find { it.code.equals(code, ignoreCase = true) }

        /**
         * Gets a country by its locale.
         * @param locale The locale to search for.
         * @return The country with the specified locale, or null if not found.
         */
        fun fromLocale(locale: String): Country? = entries.find { it.locale.equals(locale, ignoreCase = true) }

        /**
         * Gets a country by its phone prefix.
         * The prefix must start with a '+' sign.
         * @param phonePrefix The phone prefix to search for, including the '+' sign.
         * @return The country with the specified phone prefix, or null if not found.
         */
        fun fromPhonePrefix(phonePrefix: String): Country? = entries.find { it.phonePrefix.equals(phonePrefix, ignoreCase = true) }
    }
}