Excel ⇒ Oracle INSERT SQL


# データ設定仕様
null は、文字列 'null' としてセット

空のセルは、値 NULL としてセット

### 例）2023/5/2 12:23:47 のセット
 TO_TIMESTAMP('2023-05-01 12:23:47', 'YYYY-MM-DD HH24:MI:SS'))



# mkInsertSQLforOracle2.exe
全シートまとめた INSERT 文を作成⇒ Excelファイル名.sql


