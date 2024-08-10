# Excel ⇒ INSERT SQL


# 仕様
１シート⇒１テーブル
シート名⇒テーブル名
先頭行⇒カラム名
空のセルは、値 NULL としてセット
全シートまとめた INSERT 文を作成⇒ Excelファイル名.sql

日付書式⇒ DB種類に対応した変換式
日付時刻書式⇒ DB種類に対応した変換式

Excel ファイルを EXE ファイルに ドラッグ＆ドロップする。
Excel ファイルが存在したフォルダ上に、
全シートまとめた INSERT 文を作成⇒ Excelファイル名.sql
各シートのテーブル、INSERT文を作成⇒ シート名.sql


## mkInsertSQLforOracle3.exe
‐　Oracle 用 INSERT SQL生成

## mkInsertPostgreSQL.exe
‐　PostgreSQL 用 INSERT SQL生成

## mkInsertMySQL.exe
‐　MySQL 用 INSERT SQL生成
