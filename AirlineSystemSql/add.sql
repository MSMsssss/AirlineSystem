use airlineinfo;

delimiter $$
drop procedure  if exists wk;
CREATE PROCEDURE wk(
    in planeid char(6),
    in leave_time timestamp
)
BEGIN

    DECLARE seatid INT ;
    SET seatid = 1 ;
    WHILE seatid <= 20 DO
        INSERT into seatinfo values(planeid, leave_time, seatid, 0);
        SET seatid = seatid + 1 ;
    END WHILE;

END$$

delimiter ;

call wk('000002', '2019-06-01 19:09:16');
call wk('000001', '2019-06-02 16:16:05');
call wk('000003', '2019-06-01 19:42:42');
call wk('000001', '2019-06-01 16:10:15');
call wk('000004', '2019-06-02 20:25:28');
call wk('000005', '2019-06-02 20:29:58');