public enum Queries {
    SELECT_FROM_ROZLICZONE{

        @Override
        public String getQuery() {
            return "select * from dbo.__ledu_PaidInvoices";
        }
    },
    SELECT_FROM_ROZLICZONENEW {
        @Override
        public String getQuery(){
             return "SELECT nzf_Data, nzf_WartoscPierwotnaWaluta, nzf_WartoscWaluta, nzf_NumerPelny\n" +
                     "            FROM dbo.nz__Finanse where DATEDIFF(day, nzf_Data, GETDATE()) <= 60 AND (nzf_NumerPelny like 'FS%') AND nzf_WartoscWaluta <> nzf_WartoscPierwotnaWaluta AND nzf_Podtyp =1 order by nzf_NumerPelny";

        }
    },


    UPDATE_PAYMENTS {
    @Override
    public String getQuery(){
        return "UPDATE dbo.__ledu.PaidInvoices SET nzf_WartoscWaluta = ? WHERE nzf_NumerPelny = ?";
        }
    },

    INSERT_INTO_AUXILIARY {
        @Override
        public String getQuery(){
            return "INSERT INTO dbo.__ledu_PaidInvoices(nzf_NumerPelny, nzf_BLID, nzf_WartoscWaluta,nzf_WartoscPierwotnaWaluta, nzf_Data, nzf_Updated ) VALUES (?, ?, ?, ?, ?,?)";
        }
    },
    SELECT_BL_ID {
        @Override
        public String getQuery(){
            return "SELECT dok_Uwagi from dbo.dok__Dokument WHERE dok_NrPelny = ?";

        }
    },
    SET_TO_UPDATED {
        @Override
        public String getQuery(){
            return "UPDATE dbo.__ledu_PaidInvoices SET nzf_Updated = ? WHERE id = ?";
        }
    };

    public abstract String getQuery();

}
