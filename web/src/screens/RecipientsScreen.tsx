import { useState } from 'react'
import type { Recipient, Channel, Selection } from '../types'
import { CHANNEL_EMOJI, CHANNEL_LABEL, CHANNEL_COLOR, CHANNEL_COLOR_LIGHT, AVATAR_COLORS } from '../types'

const CHANNELS: Channel[] = ['sms', 'email', 'whatsapp', 'messenger']

interface Props {
  recipients: Recipient[]
  onSend: (selections: Selection[]) => void
  onBack: () => void
}

function selKey(r: Recipient, c: Channel) {
  return `${r.name}__${c}`
}

export default function RecipientsScreen({ recipients, onSend, onBack }: Props) {
  const [selected, setSelected] = useState<Set<string>>(new Set())

  function toggle(r: Recipient, c: Channel) {
    const k = selKey(r, c)
    setSelected(prev => {
      const next = new Set(prev)
      next.has(k) ? next.delete(k) : next.add(k)
      return next
    })
  }

  function handleSend() {
    const sels: Selection[] = []
    recipients.forEach(r =>
      CHANNELS.forEach(c => {
        if (selected.has(selKey(r, c))) sels.push({ recipient: r, channel: c })
      })
    )
    if (sels.length > 0) onSend(sels)
  }

  return (
    <div className="screen recipients-screen">
      <h2 className="screen-title">Envoyer à qui?</h2>

      <div className="recipients-list">
        {recipients.map((r, i) => (
          <div className="recipient-card" key={r.name}>
            <div className="recipient-header">
              <span
                className="avatar"
                style={{ background: AVATAR_COLORS[i % AVATAR_COLORS.length] }}
              >
                {r.name[0].toUpperCase()}
              </span>
              <span className="recipient-name">{r.name}</span>
            </div>

            <div className="channel-row">
              {CHANNELS.map(c => {
                const active = selected.has(selKey(r, c))
                return (
                  <button
                    key={c}
                    className="channel-btn"
                    style={{
                      background: active ? CHANNEL_COLOR[c] : CHANNEL_COLOR_LIGHT[c],
                      opacity: active ? 1 : 0.6,
                    }}
                    onClick={() => toggle(r, c)}
                    title={CHANNEL_LABEL[c]}
                    aria-label={`${r.name} via ${CHANNEL_LABEL[c]}`}
                    aria-pressed={active}
                  >
                    {CHANNEL_EMOJI[c]}
                  </button>
                )
              })}
            </div>
          </div>
        ))}
      </div>

      <button
        className="send-btn"
        onClick={handleSend}
        disabled={selected.size === 0}
      >
        ENVOYER 🚀
      </button>

      <button className="back-btn" onClick={onBack}>← Retour</button>
    </div>
  )
}
