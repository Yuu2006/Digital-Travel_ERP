-- ============================================================
-- RESET SCHEMA SACH CHO IMPORT LAI TU DAU
-- CANH BAO: Script nay xoa TOAN BO object trong schema hien tai.
-- Chi chay khi DB import dang do va da chap nhan lam sach schema.
--
-- Thu tu chay lai sau khi reset:
--   @KhoiTaoBang.sql
--   @data_v1.sql
--   @data_lien_ket.sql
--   @kiem_tra_du_lieu.sql
-- ============================================================

ROLLBACK;
ALTER SESSION SET ISOLATION_LEVEL = READ COMMITTED;

BEGIN
    FOR obj IN (
        SELECT object_name, object_type
        FROM user_objects
        WHERE object_type IN ('TRIGGER', 'PROCEDURE', 'FUNCTION', 'VIEW')
        ORDER BY CASE object_type
                     WHEN 'TRIGGER' THEN 1
                     WHEN 'PROCEDURE' THEN 2
                     WHEN 'FUNCTION' THEN 3
                     WHEN 'VIEW' THEN 4
                     ELSE 5
                 END
    ) LOOP
        BEGIN
            EXECUTE IMMEDIATE 'DROP ' || obj.object_type || ' ' || obj.object_name;
        EXCEPTION
            WHEN OTHERS THEN
                NULL;
        END;
    END LOOP;

    FOR tbl IN (
        SELECT table_name
        FROM user_tables
        ORDER BY table_name
    ) LOOP
        BEGIN
            EXECUTE IMMEDIATE 'DROP TABLE ' || tbl.table_name || ' CASCADE CONSTRAINTS PURGE';
        EXCEPTION
            WHEN OTHERS THEN
                NULL;
        END;
    END LOOP;

    FOR seq IN (
        SELECT sequence_name
        FROM user_sequences
    ) LOOP
        BEGIN
            EXECUTE IMMEDIATE 'DROP SEQUENCE ' || seq.sequence_name;
        EXCEPTION
            WHEN OTHERS THEN
                NULL;
        END;
    END LOOP;
END;
/

SELECT USER AS schema_da_reset FROM dual;

