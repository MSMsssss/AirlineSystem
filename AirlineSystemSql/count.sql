drop procedure  if exists countFullRate;

delimiter $$
CREATE PROCEDURE countFullRate(
    in planeid char(6),
    in leave_time timestamp,
    out fullrate real
)
BEGIN
declare seatnum real;
declare soldnum int;
select seat_num from flightinfo where id = planeid and flightinfo.leave_time = leave_time
into seatnum;

select count(*) from seatinfo where id = planeid and seatinfo.leave_time = leave_time and sold = 1
into soldnum;

select soldnum / seatnum into fullrate;

END$$

delimiter ;

call countFullRate('000001', '2019-05-27 16:10:15', @fullrate);