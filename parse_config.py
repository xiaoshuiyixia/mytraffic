import sys

from openai import OpenAI

client = OpenAI(api_key="your api_key")

response = client.chat.completions.create(
    model="gpt-3.5-turbo",
    messages=[
        {"role": "system", "content": "你是一个交通灯配置助手，只返回 JSON，不要解释。例如：{\"red\":10,\"green\":30,\"yellow\":3}"},
        {"role": "user", "content": f"我希望修改信号灯设置：{sys.argv[1]}"}
    ]
)

reply = response.choices[0].message.content
print(reply)
