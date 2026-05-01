package com.ledgerly.parser.core.bank

/**
 * Factory for creating bank-specific parsers based on SMS sender.
 */
object BankParserFactory {

    private val parsers = listOf(
        // Saudi Arabia Banks (Priority)
        STCBankParser(),
        RiyadBankParser(),
        AlRajhiBankParser(),
        AlinmaBankParser(),
        SNBAlAhliBankParser(),

        /* Other country banks disabled as per request
        HDFCMutualFundParser(),
        HDFCBankParser(),
        SBIBankParser(),
        SaraswatBankParser(),
        DBSBankParser(),
        IndianBankParser(),
        FederalBankParser(),
        JuspayParser(),
        SliceParser(),
        CredParser(),
        LazyPayParser(),
        UtkarshBankParser(),
        ICICIBankParser(),
        KarnatakaBankParser(),
        KeralaGraminBankParser(),
        IDBIBankParser(),
        JupiterBankParser(),
        AxisBankParser(),
        PNBBankParser(),
        PunjabSindBankParser(),
        CanaraBankParser(),
        BankOfBarodaParser(),
        BankOfIndiaParser(),
        JioPaymentsBankParser(),
        KotakBankParser(),
        IDFCFirstBankParser(),
        UnionBankParser(),
        HSBCBankParser(),
        CentralBankOfIndiaParser(),
        SouthIndianBankParser(),
        JKBankParser(),
        JioPayParser(),
        IPPBParser(),
        CityUnionBankParser(),
        IndianOverseasBankParser(),
        AirtelPaymentsBankParser(),
        IndusIndBankParser(),
        AMEXBankParser(),
        OneCardParser(),
        UCOBankParser(),
        AUBankParser(),
        YesBankParser(),
        BandhanBankParser(),
        ADCBParser(),
        FABParser(),
        EmiratesNBDParser(),
        LivBankParser(),
        CitiBankParser(),
        DiscoverCardParser(),
        OldHickoryParser(),
        LaxmiBankParser(),
        CBEBankParser(),
        AltanaFCUParser(),
        EverestBankParser(),
        BancolombiaParser(),
        MashreqBankParser(),
        CharlesSchwabParser(),
        NavyFederalParser(),
        AdelFiParser(),
        AlecuBankParser(),
        PriorbankParser(),
        NMBBankParser(),
        SiddharthaBankParser(),
        PrimeCommercialBankParser(),
        MPesaTanzaniaParser(),
        MPESAParser(),
        SelcomPesaParser(),
        TigoPesaParser(),
        CIBEgyptParser(),
        DhanlaxmiBankParser(),
        DOPBankParser(),
        HuntingtonBankParser(),
        StandardCharteredBankParser(),
        EquitasBankParser(),
        TelebirrParser(),
        ZemenBankParser(),
        DashenBankParser(),
        FaysalBankParser(),
        MelliBankParser(),
        ParsianBankParser(),
        BangkokBankParser(),
        KasikornBankParser(),
        SiamCommercialBankParser(),
        KrungThaiBankParser(),
        KrungsriBankParser(),
        TTBBankParser(),
        GSBBankParser(),
        BAACBankParser(),
        UOBThailandParser(),
        CIMBThaiParser(),
        KTCCreditCardParser(),
        TBankParser(),
        ChaseBankParser(),
        MBankCZParser(),
        BankMuscatParser(),
        GreaterBankParser()
        */
    )

    /**
     * Returns the appropriate bank parser for the given sender.
     * Returns null if no specific parser is found.
     */
    fun getParser(sender: String): BankParser? {
        return parsers.firstOrNull { it.canHandle(sender) }
    }

    /**
     * Returns the bank parser for the given bank name.
     * Returns null if no specific parser is found.
     */
    fun getParserByName(bankName: String): BankParser? {
        return parsers.firstOrNull { it.getBankName() == bankName }
    }

    /**
     * Returns all available bank parsers.
     */
    fun getAllParsers(): List<BankParser> = parsers

    /**
     * Checks if the sender belongs to any known bank.
     */
    fun isKnownBankSender(sender: String): Boolean {
        return parsers.any { it.canHandle(sender) }
    }
}
