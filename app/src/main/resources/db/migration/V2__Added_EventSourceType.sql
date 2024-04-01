drop index if exists partner_event_click_id_key;

alter table partner_event add if not exists event_source varchar(20);
create unique index IF NOT EXISTS partner_event_click_id_source_key on partner_event using btree (click_id, event_source);
UPDATE partner_event
SET event_source =  'PARTNER'
WHERE event_source is null;