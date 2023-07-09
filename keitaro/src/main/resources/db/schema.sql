create table IF NOT EXISTS partner_event (
    id uuid primary key not null default gen_random_uuid(),
    click_id character varying(254),
    status character varying(254),
    registration boolean,
    fist_replenishment boolean,
    created timestamp without time zone,
    last_change_updated timestamp without time zone
    );
create unique index IF NOT EXISTS partner_event_click_id_key on partner_event using btree (click_id);
