use airlineinfo; 

/*居民身份信息表*/
create table peopleinfo(
	id char(18) primary key not null,
    name char(20) not null
)ENGINE=MYISAM DEFAULT CHARSET=utf8;

/*用户信息表*/
create table userinfo(
	name char(15) primary key not null,
	pwd	char(20) not null,
	user_id	char(18) null,
	balance	real not null,
	foreign key(user_id) references peopleinfo(id)
)ENGINE=MYISAM DEFAULT CHARSET=utf8;

/*航班信息表*/
create table flightinfo(
    id char(6) not null,
    leave_time timestamp not null DEFAULT 0,
    arrive_time	timestamp not null DEFAULT 0,
    leave_city char(20) not null,
    arrive_city	char(20) not null,
    leave_airport char(20) not null,
    arrive_airport char(20) not null,
    price real not null,
    seat_num int not null,
    primary key(id, leave_time)
)ENGINE=MYISAM DEFAULT CHARSET=utf8;

/*航班座位表*/
create table seatinfo(
    id char(6) not null,
    leave_time timestamp not null DEFAULT 0,
    seat_id	int not null,
    sold int not null,
    foreign key(id, leave_time) references flightinfo(id, leave_time),
    primary key(id, leave_time, seat_id)
)ENGINE=MYISAM DEFAULT CHARSET=utf8;

/*订票信息表*/
create table bookinginfo(
	order_id int primary key not null AUTO_INCREMENT,
	plane_id char(6) not null,
	passenger_id char(18) not null,
	phone char(20) not null,
	leave_time timestamp not null DEFAULT 0,
	seat_id	int not null,
	returntag int not null,
	order_time timestamp not null DEFAULT 0,
	pay real not null,
	foreign key(plane_id, leave_time, seat_id) references seatinfo(id, leave_time, seat_id),
	foreign key(passenger_id) references peopleinfo(id)
)ENGINE=MYISAM DEFAULT CHARSET=utf8;

/*取票通知表*/
create table ticketinginfo(
	order_id int primary key not null,
	time timestamp not null DEFAULT 0,
	foreign key(order_id) references bookinginfo(order_id)
)ENGINE=MYISAM DEFAULT CHARSET=utf8;

/*账单表*/
create table billinfo(
	user_name char(15) not null,
	order_id int not null,
	foreign key(user_name) references userinfo(name),
	foreign key(order_id) references bookinginfo(order_id),
    primary key(user_name, order_id)
)ENGINE=MYISAM DEFAULT CHARSET=utf8;