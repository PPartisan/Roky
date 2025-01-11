create table if not exists
  users (
    id uuid default uuid_generate_v4 () primary key,
    username text unique not null,
    email text unique not null,
    password_hash text not null,
    created_at timestamp with time zone default now() not null
  );

create table if not exists
  conversations (
    id uuid default uuid_generate_v4 () primary key,
    created_at timestamp with time zone default now() not null,
    updated_at timestamp with time zone default now() not null
  );

create table if not exists
  conversation_participants (
    conversation_id uuid references conversations (id) on delete cascade,
    user_id uuid references users (id) on delete cascade,
    created_at timestamp with time zone default now() not null,
    primary key (conversation_id, user_id)
  );

create table if not exists
  messages (
    id uuid default uuid_generate_v4 () primary key,
    conversation_id uuid references conversations (id) on delete cascade,
    user_id uuid references users (id) on delete cascade,
    content text not null,
    created_at timestamp with time zone default now() not null,
    updated_at timestamp with time zone default now() not null
  );

drop index if exists idx_messages_conversation_id;

create index idx_messages_conversation_id on messages (conversation_id);

drop index if exists idx_messages_created_at;

create index idx_messages_created_at on messages (created_at);

drop index if exists idx_conversation_participants_user_id;

create index idx_conversation_participants_user_id on conversation_participants (user_id);

alter table users enable row level security;

alter table conversations enable row level security;

alter table conversation_participants enable row level security;

alter table messages enable row level security;

DROP POLICY IF EXISTS "Users can view their own data" ON users;
DROP POLICY IF EXISTS "Users can view conversations they're part of" ON conversations;
DROP POLICY IF EXISTS "Users can view messages in their conversations" ON messages;

CREATE POLICY "Users can view their own data" ON users FOR
SELECT
  USING (auth.uid() = id);

CREATE POLICY "Users can view conversations they're part of" ON conversations FOR
SELECT
  USING (
    EXISTS (
      SELECT 1
      FROM conversation_participants
      WHERE conversation_id = conversations.id
      AND user_id = auth.uid()
    )
  );

CREATE POLICY "Users can view messages in their conversations" ON messages FOR
SELECT
  USING (
    EXISTS (
      SELECT 1
      FROM conversation_participants
      WHERE conversation_id = messages.conversation_id
      AND user_id = auth.uid()
    )
  );
-- First drop the trigger if it exists
DROP TRIGGER IF EXISTS update_conversation_timestamp ON messages;

-- Then drop the function if it exists
DROP FUNCTION IF EXISTS update_conversation_timestamp();

-- Recreate the function
CREATE OR REPLACE FUNCTION update_conversation_timestamp()
RETURNS TRIGGER AS $$
BEGIN
  UPDATE conversations
  SET updated_at = now()
  WHERE id = NEW.conversation_id;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Recreate the trigger
CREATE TRIGGER update_conversation_timestamp
AFTER INSERT OR UPDATE ON messages
FOR EACH ROW
EXECUTE FUNCTION update_conversation_timestamp();