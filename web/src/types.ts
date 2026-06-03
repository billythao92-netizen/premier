export interface Recipient {
  name: string
  phone: string
  email: string
}

export type Channel = 'sms' | 'email' | 'whatsapp' | 'messenger'

export interface Selection {
  recipient: Recipient
  channel: Channel
}

export const CHANNEL_EMOJI: Record<Channel, string> = {
  sms: '📱',
  email: '📧',
  whatsapp: '💚',
  messenger: '💬',
}

export const CHANNEL_LABEL: Record<Channel, string> = {
  sms: 'SMS',
  email: 'Email',
  whatsapp: 'WhatsApp',
  messenger: 'Messenger',
}

export const CHANNEL_COLOR: Record<Channel, string> = {
  sms:       '#1565C0',
  email:     '#C62828',
  whatsapp:  '#2E7D32',
  messenger: '#1565C0',
}

export const CHANNEL_COLOR_LIGHT: Record<Channel, string> = {
  sms:       '#90CAF9',
  email:     '#EF9A9A',
  whatsapp:  '#A5D6A7',
  messenger: '#90CAF9',
}

export const DEFAULT_RECIPIENTS: Recipient[] = [
  { name: 'Maman',     phone: '+15551234567', email: 'maman@example.com' },
  { name: 'Papa',      phone: '+15559876543', email: 'papa@example.com' },
  { name: 'Grand-Mère',phone: '+15555550001', email: 'grandmere@example.com' },
]

export const AVATAR_COLORS = ['#4FC3F7', '#FF6B35', '#66BB6A', '#AB47BC', '#FF7043']
