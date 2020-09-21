-- pod osiemnastką musi być nazwa C##
CREATE USER c##akademik IDENTIFIED BY akademik;
GRANT CONNECT, RESOURCE to c##akademik;
GRANT UNLIMITED TABLESPACE TO c##akademik;
