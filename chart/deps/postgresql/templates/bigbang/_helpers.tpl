{{/*
Bigbang labels
*/}}
{{- define "bigbang.labels" -}}
app: {{ template "postgresql.name" . }}
{{- if .Chart.AppVersion }}
version: {{ .Chart.AppVersion | quote }}
{{- end }}
{{- end }}