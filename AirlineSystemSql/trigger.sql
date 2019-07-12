drop trigger if exists neworder;
drop trigger if exists t1;

create trigger neworder after insert on bookinginfo
for each row select new.order_id into @neworder_id;

delimiter $$
create trigger t1 before update on seatinfo
for each row begin
if old.sold = 1 and new.sold = 1 then
    select 'error' into @msg;
else 
    select 'success' into @msg;
end if;
end$$

delimiter ;