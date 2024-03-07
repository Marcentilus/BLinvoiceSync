public enum Queries {
    SELECT_FROM_ROZLICZONE{

        @Override
        public String getQuery() {
            return "select * from rozliczone";
        }
    },
    SELECT_FROM_ROZLICZONENEW {
        @Override
        public String getQuery(){
            /* return "select nzf_Data, nzf_WartoscPierwotnaWaluta, nzf_WartoscWaluta, nzf_NumerPelny from rozliczonenew where DATEDIFF(day, nzf_Data, GETDATE()) <= 30 AND (nzf_NumerPelny like 'FS%') AND nzf_WartoscWaluta > 0 order by nzf_NumerPelny";*/
            return "select nzf_Data, nzf_WartoscPierwotnaWaluta, nzf_WartoscWaluta, nzf_NumerPelny from rozliczonenew where DATEDIFF(nzf_Data, CURDATE()) <= 30 AND (nzf_NumerPelny like 'FS%') AND nzf_WartoscWaluta > 0 order by nzf_NumerPelny";
        }
    },

    DELETE_ALL {
        @Override
        public String getQuery(){
            return "delete from rozliczone";
        }
    },
    INSERT_INTO_AUXILIARY {
        @Override
        public String getQuery(){
            return "INSERT INTO rozliczone(nzf_WartoscWaluta, nzf_NumerPelny) VALUES (?, ?)";
        }
    },
    SELECT_BL_ID {
        @Override
        public String getQuery(){
            return "SELECT dok_Uwagi from dokumenty WHERE dok_NumerPelny = ?";
        }
    };
    public abstract String getQuery();
}
