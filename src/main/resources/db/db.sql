create table roles
(
	role_id uuid default gen_random_uuid() not null,
	role_name varchar(50) not null,
	description varchar(255) not null,
	primary key (role_id)
);

alter table roles owner to postgres;

create table users
(
	user_id uuid default gen_random_uuid() not null,
	email varchar(150) not null,
	password_hash varchar(255) not null,
	role_id uuid not null,
	created_at timestamp with time zone default CURRENT_TIMESTAMP not null,
	primary key (user_id),
	unique (email),
	constraint fk_role_in_users
		foreign key (role_id) references roles
			on delete restrict
);

alter table users owner to postgres;

create index index_user_role_id
	on users (role_id);

create table category
(
	category_id uuid default gen_random_uuid() not null,
	name varchar(100) not null,
	description varchar(255) not null,
	created_at timestamp with time zone default CURRENT_TIMESTAMP not null,
	updated_at timestamp with time zone default CURRENT_TIMESTAMP not null,
	primary key (category_id),
	unique (name)
);

alter table category owner to postgres;

create index index_category_name
	on category (name);

create table product
(
	product_id uuid default gen_random_uuid() not null,
	name varchar(100) not null,
	description text not null,
	price numeric(10,2) not null,
	stock_quantity integer not null,
	category_id uuid not null,
	created_at timestamp with time zone default CURRENT_TIMESTAMP not null,
	updated_at timestamp with time zone default CURRENT_TIMESTAMP not null,
	primary key (product_id),
	constraint fk_category_in_product
		foreign key (category_id) references category
			on delete restrict,
	constraint product_price_check
		check (price > (0)::numeric),
	constraint product_stock_quantity_check
		check (stock_quantity > 0)
);

alter table product owner to postgres;

create index index_product_category_id
	on product (category_id);

create index index_product_name
	on product (name);

create table customer
(
	customer_id uuid default gen_random_uuid() not null,
	user_id uuid not null,
	first_name varchar(150) not null,
	last_name varchar(150),
	phone varchar(30) not null,
	is_active boolean default true not null,
	primary key (customer_id),
	unique (user_id),
	constraint fk_user_in_customer
		foreign key (user_id) references users
			on delete cascade
);

alter table customer owner to postgres;

create table review
(
	review_id uuid default gen_random_uuid() not null,
	product_id uuid not null,
	customer_id uuid not null,
	rating integer not null,
	comment jsonb,
	created_at timestamp with time zone default CURRENT_TIMESTAMP not null,
	primary key (review_id),
	constraint uq_review_customer_product
		unique (product_id, customer_id),
	constraint fk_product_in_review
		foreign key (product_id) references product
			on delete cascade,
	constraint fk_customer_in_review
		foreign key (customer_id) references customer
			on delete cascade,
	constraint review_rating_check
		check ((rating >= 1) AND (rating <= 5))
);

alter table review owner to postgres;

create index index_review_product_id
	on review (product_id);

create index index_review_customer_id
	on review (customer_id);

create table order_statuses
(
	status_id uuid default gen_random_uuid() not null,
	status_name varchar(50) not null,
	description text,
	primary key (status_id),
	unique (status_name)
);

alter table order_statuses owner to postgres;

create table orders
(
	order_id uuid default gen_random_uuid() not null,
	customer_id uuid not null,
	order_date timestamp with time zone default CURRENT_TIMESTAMP not null,
	total_amount numeric(10,2) not null,
	shipping_country varchar(100),
	shipping_city varchar(100),
	shipping_postal_code varchar(100) not null,
	status_id uuid,
	primary key (order_id),
	constraint fk_customer_in_orders
		foreign key (customer_id) references customer
			on delete restrict,
	constraint fk_order_status
		foreign key (status_id) references order_statuses,
	constraint orders_total_amount_check
		check (total_amount > (0)::numeric)
);

alter table orders owner to postgres;

create index index_orders_customer_id
	on orders (customer_id);

create index index_orders_order_date
	on orders (order_date);

create table order_item
(
	order_item_id uuid default gen_random_uuid() not null,
	order_id uuid not null,
	product_id uuid not null,
	quantity integer not null,
	price_at_purchase numeric(10,2) not null,
	primary key (order_item_id),
	constraint fk_order_in_order_item
		foreign key (order_id) references orders
			on delete cascade,
	constraint fk_product_in_order_item
		foreign key (product_id) references product
			on delete restrict,
	constraint order_item_quantity_check
		check (quantity > 0),
	constraint order_item_price_at_purchase_check
		check (price_at_purchase > (0)::numeric)
);

alter table order_item owner to postgres;

create index index_order_item_order_id
	on order_item (order_id);

create index index_order_item_product_id
	on order_item (product_id);

create table cart
(
	cart_id uuid not null,
	customer_id uuid not null,
	created_at timestamp not null,
	updated_at timestamp not null,
	primary key (cart_id),
	constraint fk_cart_customer
		foreign key (customer_id) references customer
);

alter table cart owner to postgres;

create table cart_item
(
	cart_item_id uuid not null,
	cart_id uuid not null,
	product_id uuid not null,
	quantity integer not null,
	added_at timestamp not null,
	primary key (cart_item_id),
	constraint fk_cartitem_cart
		foreign key (cart_id) references cart,
	constraint fk_cartitem_product
		foreign key (product_id) references product
);

alter table cart_item owner to postgres;


