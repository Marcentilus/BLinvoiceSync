public enum Queries {
    SELECT_FROM_ROZLICZONE{

        @Override
        public String getQuery() {
            return "select * from dbo.__ledu_SettledInvoices";
        }
    },
    SELECT_FROM_ROZLICZONENEW {
        @Override
        public String getQuery(){
             return "SELECT nzf_Data, nzf_WartoscPierwotnaWaluta, nzf_WartoscWaluta, nzf_NumerPelny\n" +
                     "            FROM dbo.nz__Finanse where DATEDIFF(day, nzf_Data, GETDATE()) <= 30 AND (nzf_NumerPelny like 'FS%') AND nzf_WartoscWaluta > 0 order by nzf_NumerPelny";

        }
    },

    DELETE_ALL {
        @Override
        public String getQuery(){
            return "delete from dbo.__ledu_SettledInvoices";
        }
    },
    INSERT_INTO_AUXILIARY {
        @Override
        public String getQuery(){
            return "INSERT INTO [Kopia_Leduvel].[dbo].__ledu_SettledInvoices(WartoscWaluta, NumerPelny) VALUES (?, ?)";
        }
    },
    SELECT_BL_ID {
        @Override
        public String getQuery(){
            return "SELECT dok_Uwagi from dbo.dok__Dokument WHERE dok_NrPelny = ?";
        }
    };
    public abstract String getQuery();
}
